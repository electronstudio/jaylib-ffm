/**
     * {{ function.description }}
     */
    public static {{ function.java_return_type }} {{ function.java_name }}({% for param in function.params %}
            {{ param.java_type }} {{ param.name }}{{ ", " if not loop.last else "" }} {% endfor %}
        ){
            {% if function.java_return_type == "void" %}{% include 'result.java.frag' %};
            {% elif function.return_type_is_a_struct %} return new {{ function.java_return_type }}({% include 'result.java.frag' %});
            {% else %}return {% include 'result.java.frag' %};{% endif %}
    }
    {% if function.needs_allocator %}
    /**
     * {{ function.description }}
     */
    public static {{ function.java_return_type }} {{ function.java_name }}(Arena arena{{ ", " if function.params else "" }} {% for param in function.params %}
            {{ param.java_type }} {{ param.name }}{{ ", " if not loop.last else "" }} {% endfor %}
        ){
            {% if function.java_return_type == "void" %}{% include 'result.java.frag' %};
            {% elif function.return_type_is_a_struct %} return new {{ function.java_return_type }}({% include 'result_arena.java.frag' %});
            {% else %}return {% include 'result.java.frag' %};{% endif %}
    }
    {% endif %}