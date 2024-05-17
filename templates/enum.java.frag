public static class {{ enum['name'] }}{
        {% for subenum in enum['values'] %}
        public static final int {{ subenum['name'] }} = {{ subenum['value'] }};{% endfor %}
    }