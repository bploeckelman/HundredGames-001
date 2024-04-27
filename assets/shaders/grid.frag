#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_res;
uniform vec3 u_mouse;

varying vec4 v_color;
varying vec2 v_texCoord;


void main() {

    vec4 image = texture2D(u_texture, v_texCoord);

    vec2 p = v_texCoord * u_res;

    float r = length(u_mouse.xy - p);

    image.a *= smoothstep( 500. , 50., r);

    gl_FragColor = image * v_color;
}
