/** {{ enum['description'] }} */
public static class {{ enum['name'] }}{
        {% for subenum in enum['values'] %}
        /** {{ subenum['description'] }} */
        public static final int {{ subenum['name'] }} = {{ subenum['value'] }};{% endfor %}
    }