import json




def c_type_to_java_type(field_type):
    match field_type:
        case "bool":
            return "boolean"
        case "unsigned char":
            return "byte"
        case "float":
            return "float"
        case "unsigned int":
            return "int"
        case "int":
            return "int"
        case _:
            print("unknown field_type", field_type)
            return "MemorySegment"

def get_field_data(field):
    field_name = field['name']
    field_name_cap = field_name[0].upper() + field_name[1:]
    field_type = field['type']
    java_type = c_type_to_java_type(field_type)
    return field_name, field_name_cap, field_type, java_type

jf = open('raylib_api.json')


data = json.load(jf)


for struct in data['structs']:
    struct_name = struct['name']
    f = open("src/main/java/com/raylib/" + struct_name + ".java", "w")

    f.write("""
package com.raylib;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
    
public class $STRUCT_NAME$ {
    public final MemorySegment memorySegment;
    public $STRUCT_NAME$() {
        memorySegment = com.raylib.jextract.$STRUCT_NAME$.allocate(Arena.ofAuto());
    }
    public $STRUCT_NAME$(MemorySegment memorySegment){
        this.memorySegment = memorySegment;
    }
    """.replace("$STRUCT_NAME$", struct_name))

    f.write("public "+struct_name+"(")
    for field in struct['fields']:
        field_name, field_name_cap, field_type, java_type = get_field_data(field)
        f.write(java_type+" "+field_name)
        if field != struct['fields'][-1]:
            f.write(", ")
    f.write(") {")
    f.write("""
        memorySegment = com.raylib.jextract.$STRUCT_NAME$.allocate(Arena.ofAuto());
    """.replace("$STRUCT_NAME$", struct_name))

    for field in struct['fields']:
        field_name, field_name_cap, field_type, java_type = get_field_data(field)
        f.write("""
        set$FIELD_NAME_CAP$($FIELD_NAME$);
        """.replace("$FIELD_NAME$", field_name).
            replace("$STRUCT_NAME$", struct_name).
            replace("$FIELD_NAME_CAP$", field_name_cap).
            replace("$TYPE$", java_type))

    f.write("}")

    for field in struct['fields']:
        field_name, field_name_cap, field_type, java_type = get_field_data(field)
        f.write("""
    public $TYPE$ get$FIELD_NAME_CAP$() {
        return com.raylib.jextract.$STRUCT_NAME$.$FIELD_NAME$(memorySegment);
    }

    public void set$FIELD_NAME_CAP$($TYPE$ value){
        com.raylib.jextract.$STRUCT_NAME$.$FIELD_NAME$(memorySegment, value);
    }  
        """.replace("$FIELD_NAME$", field_name).
                replace("$STRUCT_NAME$", struct_name).
                replace("$FIELD_NAME_CAP$", field_name_cap).
                replace("$TYPE$", java_type))

    f.write("""
}
    """)

    f.close()

f = open("src/main/java/com/raylib/Raylib.java", "w")

f.write("""
package com.raylib;

public class Raylib{

}
""")

f.close()


# Closing file
jf.close()
