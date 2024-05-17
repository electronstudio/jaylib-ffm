{{ function.name }}({% if function.needs_allocator %}Arena.ofAuto(){% if function.params %}, {% endif %}{% endif %}
            {% for param in function.params %}{{ param.converter_to_c_type }}{{ ", " if not loop.last else "" }}{% endfor %}
        ){% if function.java_return_type == "String"%}.getString(0){% endif %}