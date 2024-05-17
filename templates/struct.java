package com.raylib;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class {{ struct_name }} {
    public final MemorySegment memorySegment;
    public {{ struct_name }}() {
        memorySegment = com.raylib.jextract.{{ struct_name }}.allocate(Arena.ofAuto());
    }
    public {{ struct_name }}(MemorySegment memorySegment){
        this.memorySegment = memorySegment;
    }


    public {{ struct_name }}(
        {% for field in fields %} {{ field.java_type }} {{ field.name }}{{ ", " if not loop.last else "" }}
        {% endfor %}
        ){
        memorySegment = com.raylib.jextract.{{ struct_name }}.allocate(Arena.ofAuto());

        {% for field in fields %}
        {{ field.setter }}({{ field.name }});
        {% endfor %}
    }


        {% for field in fields %}
        public {{ field.java_type }} {{ field.getter }}() {
                {% if field.is_a_struct %}
                return new {{ field.java_type }}(com.raylib.jextract.{{struct_name}}.{{field.name}}(memorySegment));
                {% else %}
                return com.raylib.jextract.{{struct_name}}.{{field.name}}(memorySegment);
                {% endif %}
    }

        public void {{ field.setter }}({{ field.java_type }} value){
                com.raylib.jextract.{{ struct_name }}.{{ field.name }}(memorySegment,{{ field.value_to_c_type }});
    }
        {% endfor %}


}
    