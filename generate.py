import json

from jinja2 import Environment, FileSystemLoader

# Image * is an array only once, SetWindowIcons.
# void * is usually a buffer, could convert?  sometimes it is a pointer to a pixel in a buffer etc

LEGACY_NAMES=False

def c_type_to_java_type(field_type):
    match field_type:
        case "bool":
            return "boolean"
        case "unsigned char":
            return "byte"
        case "char":
            return "byte"
        case "float":
            return "float"
        case "float *":
            return "java.nio.FloatBuffer"
        case "float[2]":  # TODO test if arrays are working, byte order might be wrong
            return "float[]"
        case "float[4]":
            return "float[]"
        case "double":
            return "double"
        case "unsigned int":
            return "int"
        case "int":
            return "int"
        case "int *":
            return "java.nio.IntBuffer"
        case "long":
            return "long"
        case "void":
            return "void"
        case "const char *":
            return "String"
        # case "Camera3D *":  # currently always a pointer to one, never an array
        #     return "Camera3D"
        case "unsigned char *":
            return "java.nio.ByteBuffer"
        case "const unsigned char *":
            return "java.nio.ByteBuffer"
        case "char *":                      # C char is not same as Java char, so we just use bytes
            return "java.nio.ByteBuffer"    # We could convert to String in SOME cases, but honestly noone should be using Raylib for string manipulation
        case "...":
            raise Exception("dont support varargs")
        case _:
            # if field_type in struct_names_pointers:
            #     return field_type[:-2]
            # el
            if field_type in struct_names:
                return field_type
            elif field_type in struct_names_pointers:
                return field_type[:-2]
            else:
                print(f"WARNING: unknown field_type {field_type}.")
                return "MemorySegment"


def converter_to_c_type(field_type, field_name):  # could we do this on javatype?
    if field_type in aliases:
        field_type = aliases[field_type]
    if field_type in struct_names:
        return field_name+".memorySegment"
    elif field_type in struct_names_pointers:
        return field_name+".memorySegment"
    else:
        match field_type:
            case "const char *":
                return "localArena.allocateFrom(" + field_name + ")"
            case "float *":
                return "MemorySegment.ofBuffer(" + field_name + ")"
            case "int *":
                return "MemorySegment.ofBuffer(" + field_name + ")"
            case "char *":
                return "MemorySegment.ofBuffer(" + field_name + ")"
            case "unsigned char *":
                return "MemorySegment.ofBuffer(" + field_name + ")"
            case "const unsigned char *":
                return "MemorySegment.ofBuffer(" + field_name + ")"
            case "float[2]":
                return "Arena.ofAuto().allocateFrom(ValueLayout.JAVA_FLOAT, " + field_name + ")"  # FIXME localArena would better
            case "float[4]":
                return "Arena.ofAuto().allocateFrom(ValueLayout.JAVA_FLOAT, " + field_name + ")"  # FIXME localArena would better
            case _:
                return field_name

def converter_from_memorysegment(java_type):
    match java_type:
        case "String":
            return ".getString(0)"
        case "java.nio.FloatBuffer":
            return ".reinterpret(Integer.MAX_VALUE/2).asByteBuffer().order(ByteOrder.nativeOrder()).asFloatBuffer()"
        case "java.nio.IntBuffer":
            return ".reinterpret(Integer.MAX_VALUE/2).asByteBuffer().order(ByteOrder.nativeOrder()).asIntBuffer()"
        case "java.nio.ByteBuffer":
            return ".reinterpret(Integer.MAX_VALUE/2).asByteBuffer().order(ByteOrder.nativeOrder())"
        case "float[]":
            return ".toArray(ValueLayout.JAVA_FLOAT)"
        case _:
            return ""


class Field:
    def __init__(self, name, type, description=""):
        self.name = name
        if type in aliases:
            type = aliases[type]
        self.type = type
        self.description = description
        self.getter = "get" + name[0].upper() + name[1:]
        self.setter = "set" + name[0].upper() + name[1:]
        self.java_type = c_type_to_java_type(type)
        self.converter_to_c_type = converter_to_c_type(type, name)
        self.value_to_c_type = converter_to_c_type(type, "value")
        self.needs_local_allocator = type == "const char *"
        self.is_a_struct = type in struct_names
        self.is_a_struct_pointer = type in struct_names_pointers
        self.converter_from_memorysegment = converter_from_memorysegment(self.java_type)

class Function:
    def __init__(self, name, return_type, description, params=[]):
        self.name = name
        self.java_name = name[0].lower() + name[1:]
        if LEGACY_NAMES:
            self.java_name = name
        if return_type in aliases:
            return_type = aliases[return_type]
        self.return_type = return_type
        self.java_return_type = c_type_to_java_type(return_type)
        self.return_type_is_a_struct = self.java_return_type in struct_names
        self.params = params
        self.description = description
        self.needs_allocator = f"public static MemorySegment {name}(SegmentAllocator allocator" in raylib_h
        self.needs_local_allocator = any(x.needs_local_allocator for x in params)
        self.converter_from_memorysegment = converter_from_memorysegment(self.java_return_type)


with open("src/main/java/com/raylib/jextract/raylib_h.java", 'r') as file:
    raylib_h = file.read()

with open("src/main/java/com/raylib/jextract/raylib_h_1.java", 'r') as file:
    raylib_h += file.read()

jf = open('raylib_api.json')

data = json.load(jf)

struct_names = []
aliases = {}

for struct in data['structs']:
    struct_names.append(struct['name'])

for alias in data['aliases']:
    aliases[alias['name']] = alias['type']
    aliases[alias['name']+" *"] = alias['type']+" *"

struct_names_pointers = [x + " *" for x in struct_names]

environment = Environment(loader=FileSystemLoader("templates/"))
template = environment.get_template("struct.java")

for struct in data['structs']:
    struct_name = struct['name']
    struct_description = struct['description']
    fields = []
    for field in struct['fields']:
        fields.append(Field(field['name'], field['type'], field['description']))

    content = template.render(struct_name=struct_name, fields=fields, struct_description=struct_description)
    with open("src/main/java/com/raylib/" + struct_name + ".java", "w") as f:
        f.write(content)

functions = []
for function in data['functions']:
    params = []
    try:
        if 'params' in function:
            for param in function['params']:
                params.append(Field(param['name'], param['type'], ""))
        functions.append(Function(function['name'], function['returnType'], function['description'], params))
    except Exception as e:
        print(f"WARNING: skipping function {function['name']} because {e}")

with open("src/main/java/com/raylib/Raylib.java", "w") as f:
    f.write(environment.get_template("Raylib.java").render(functions=functions, struct_names=struct_names, enums=data['enums']))

