#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform vec4 u_color1;
uniform vec4 u_color2;

varying vec4 v_color;
varying vec2 v_texCoord;


void main() {

    vec2 p = v_texCoord * 2. - 1.;

    float r = length(p);
    float a = atan(p.y,p.x) + u_time + r/1.;


    vec2 uv = vec2( 0.2/r + 0.4*u_time, a/3.14 );
    vec4 noise = texture2D(u_texture, uv);

    vec3 color = mix(u_color2.xyz, u_color1.xyz, noise.y);
    float alpha = smoothstep(1.01, .8, r);
    alpha *= smoothstep(.04, .3, r);
    alpha *= 1. - (smoothstep(.3, .6, r) * noise.x);
    gl_FragColor = vec4(color, alpha) * v_color;
}
