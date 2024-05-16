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
        case "...":
            raise Exception("dont support varargs")
        case _:
            print("WARNING: unknown field_type", field_type)
            return "MemorySegment"

def converter_to_c_type(field_type, field_name):
    match field_type:
        case "const char *":
            return "Arena.ofAuto().allocateFrom("+field_name+")"
        case _:
            return field_name

class Field:
    def __init__(self, name, type, description=""):
        self.name = name
        self.type = type
        self.description = description
        self.getter = "get"+name[0].upper() + name[1:]
        self.setter = "set"+name[0].upper() + name[1:]
        self.java_type = c_type_to_java_type(type)
        self.converter_to_c_type = converter_to_c_type(type, name)



class Function:
    def __init__(self, name, return_type, description="",  params=[]):
        self.name = name
        self.java_name = name[0].lower() + name[1:]
        self.return_type = return_type
        self.java_return_type = c_type_to_java_type(return_type)
        self.params=params
        self.description = description
        self.needs_allocator = f"public static MemorySegment {name}(SegmentAllocator allocator" in raylib_h


with open("src/main/java/com/raylib/jextract/raylib_h.java", 'r') as file:
    raylib_h = file.read()

jf = open('raylib_api.json')


data = json.load(jf)

environment = Environment(loader=FileSystemLoader("templates/"))
template = environment.get_template("struct.java")

for struct in data['structs']:
    struct_name = struct['name']
    fields = []
    for field in struct['fields']:
        fields.append(Field(field['name'], field['type'], field['description']))

    content = template.render(struct_name=struct_name, fields=fields)
    with open("src/main/java/com/raylib/" + struct_name + ".java", "w") as f:
        f.write(content)

functions = []
for function in data['functions']:
    params = []
    try:
        if 'params' in function:
            for param in function['params']:
                params.append(Field(param['name'], param['type']))
        functions.append(Function(function['name'], function['returnType'], function['description'], params))
    except Exception as e:
        print(f"WARNING: skipping function {function['name']} {e}")

with open("src/main/java/com/raylib/Raylib.java", "w") as f:
    f.write(environment.get_template("Raylib.java").render(functions=functions))

