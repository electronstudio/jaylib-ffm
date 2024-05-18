import json

from jinja2 import Environment, FileSystemLoader


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
        case "double":
            return "double"
        case "unsigned int":
            return "int"
        case "int":
            return "int"
        case "long":
            return "long"
        case "void":
            return "void"
        case "const char *":
            return "String"
        case "Camera3D *":  # currently always a pointer to one, never an array
            return "Camera3D"
        case "unsigned char *":  # some functions return buffers of untyped bytes, best kept as MemorySegment?
            return "MemorySegment"
        case "const unsigned char *":  # other functions take those buffers as argument
            return "MemorySegment"
        case "...":
            raise Exception("dont support varargs")
        case _:
            # if field_type in struct_names_pointers:
            #     return field_type[:-2]
            # el
            if field_type in struct_names:
                return field_type
            else:
                print(f"WARNING: unknown field_type {field_type}.")
                return "MemorySegment"


def converter_to_c_type(field_type, field_name):
    if field_type in aliases:
        field_type = aliases[field_type]
    if field_type in struct_names:
        return field_name+".memorySegment"
    elif field_type == "Camera3D *":
        return field_name+".memorySegment"
    else:
        match field_type:
            case "const char *":
                return "Arena.ofAuto().allocateFrom(" + field_name + ")"
            case _:
                return field_name


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
        self.is_a_struct = type in struct_names


class Function:
    def __init__(self, name, return_type, description, params=[]):
        self.name = name
        self.java_name = name[0].lower() + name[1:]
        if return_type in aliases:
            return_type = aliases[return_type]
        self.return_type = return_type
        self.java_return_type = c_type_to_java_type(return_type)
        self.return_type_is_a_struct = self.java_return_type in struct_names
        self.params = params
        self.description = description
        self.needs_allocator = f"public static MemorySegment {name}(SegmentAllocator allocator" in raylib_h


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

# struct_names_pointers = [x + " *" for x in struct_names]

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

