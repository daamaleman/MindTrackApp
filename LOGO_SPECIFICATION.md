# 🧠 MindTrack Logo - Guía Completa y Especificaciones

## 📋 Descripción del Logo

**Concepto:** Pulso cardíaco/mental suavizado con ecos de onda cerebral (EEG), transmitiendo seguimiento del bienestar emocional y mental.

**Características principales:**
- ✅ Onda suavizada y armónica con curvas Bézier fluidas
- ✅ Gradiente sutil de morado (violeta claro #9B7FD9 → morado profundo #6D4FA8)
- ✅ Trazos redondeados (stroke-linecap y stroke-linejoin: round)
- ✅ Silueta sutil de perfil humano (cabeza)
- ✅ Diseño minimalista, plano y 100% escalable
- ✅ Transmite: profesionalidad, calma, confiabilidad, innovación

---

## 🎨 Paleta de Colores

| Elemento | Código Hexadecimal | RGB | Uso |
|----------|-------------------|-----|-----|
| Fondo Principal | #7C5CFF | 124, 92, 255 | Fondo app |
| Gradiente Claro | #9B7FD9 | 155, 125, 217 | Líneas externas |
| Gradiente Oscuro | #6D4FA8 | 109, 79, 168 | Pico principal |
| Blanco | #FFFFFF | 255, 255, 255 | Círculo/texto |
| Gris Neutro | #E8E8E8 | 232, 232, 232 | Bordes sutiles |

---

## 📁 Versiones Disponibles

### 1. **mindtrack_icon_only.svg** 🔘
- **Uso:** Ícono de app, favicon, redes sociales
- **Tamaño:** 200×200px (escalable)
- **Contenido:** Solo círculo blanco con onda
- **Fondo:** Transparente (agregar color según contexto)

### 2. **mindtrack_logo_main.svg** 🎯
- **Uso:** Logo principal con fondo púrpura
- **Tamaño:** 200×200px
- **Contenido:** Ícono + fondo púrpura
- **Variante:** Versión completa marca

### 3. **mindtrack_logo_with_text.svg** 📝
- **Uso:** Logo con texto, encabezados, marketing
- **Tamaño:** 400×240px
- **Contenido:** Ícono + "MIND TRACK" en tipografía limpia
- **Estilo:** Moderno y profesional

### 4. **mindtrack_logo_light_bg.svg** ☀️
- **Uso:** Versión para fondos claros/blancos
- **Tamaño:** 200×200px
- **Contenido:** Mismo ícono optimizado para legibilidad en fondos claros
- **Variante:** Bordes y sombras sutiles

---

## 🔍 Detalles Técnicos

### Dimensiones del Ícono
- **Viewport:** 108×108 px (estándar Android)
- **Radio del círculo:** 65px
- **Ancho de línea (onda):** 2.8-3.5px
- **Altura de picos:** Primeros -6px, pico principal -19px

### Curvas de la Onda
```
Segmento 1: Línea base inicial (suave)
Segmento 2: Pico pequeño (ondulación ECG)
Segmento 3: Valle pequeño (variación)
Segmento 4: Pico grande principal (latido/actividad mental)
Segmento 5: Segunda ondulación (armónica)
Segmento 6: Línea base final (regreso a equilibrio)
```

### Opciones de Trazo
- **strokeLineCap:** round (redondeado en extremos)
- **strokeLineJoin:** round (uniones redondeadas)
- **strokeWidth:** 2.8-4px (según contexto)

---

## 💡 Uso Recomendado

### ✅ Dónde Usar Cada Versión

| Contexto | Versión | Notas |
|----------|---------|-------|
| App Launcher Android | ic_launcher_foreground.xml | Integrado en sistema |
| Favicon Web | mindtrack_icon_only.svg | Fondo transparente |
| Social Media | mindtrack_logo_main.svg | Con fondo púrpura |
| Papelería/Marketing | mindtrack_logo_with_text.svg | Incluye tipografía |
| Documentos claros | mindtrack_logo_light_bg.svg | Optimizado para papel |
| Sitio Web Light | mindtrack_logo_light_bg.svg | Fondo blanco |

---

## 🎯 Directrices de Diseño

### ✨ Espaciado Mínimo
- Dejar al menos **20% del tamaño** del logo de espacio vacío alrededor
- No sobrecargar con otros elementos

### 📏 Tamaños Recomendados
- **Mínimo:** 32×32px (en pantalla de móvil)
- **Ideal:** 64×64px - 192×192px (máxima definición)
- **Máximo:** Sin límite (es vectorial)

### 🎨 Variaciones de Color Aceptables
- **Monocromo blanco** (sobre fondos oscuros)
- **Monocromo púrpura** (sobre fondos claros)
- **Gradiente original** (recomendado)
- **NO** modificar a otros colores (mantener identidad)

### ❌ Lo Que NO Hacer
- ❌ Cambiar colores a versiones no aprobadas
- ❌ Distorsionar proporções
- ❌ Simplificar excesivamente
- ❌ Agregar efectos de sombra pesados
- ❌ Usar en tamaños menores a 32px sin revisar legibilidad

---

## 🔄 Formatos Disponibles

| Formato | Archivo | Escalabilidad | Uso Recomendado |
|---------|---------|---------------|-----------------|
| SVG | *.svg | ✅ Perfecta | Web, marketing, impresión |
| Android Vector | ic_launcher_foreground.xml | ✅ Perfecta | App móvil Android |
| PNG | (convertir de SVG) | ❌ Limitada | Web legacy, redes |
| WebP | (ya instalado) | ✅ Muy buena | App Android, web móvil |

---

## 📱 Integración Android

### Ubicación del Logo
```
app/src/main/res/
├── drawable/
│   ├── ic_launcher_background.xml    [Fondo púrpura]
│   └── ic_launcher_foreground.xml    [Logo mejorado ✨ NUEVO]
├── mipmap-mdpi/
│   ├── ic_launcher.webp              [48×48px]
│   └── ic_launcher_round.webp
├── mipmap-hdpi/
│   ├── ic_launcher.webp              [72×72px]
│   └── ic_launcher_round.webp
├── mipmap-xhdpi/
│   ├── ic_launcher.webp              [96×96px]
│   └── ic_launcher_round.webp
├── mipmap-xxhdpi/
│   ├── ic_launcher.webp              [144×144px]
│   └── ic_launcher_round.webp
├── mipmap-xxxhdpi/
│   ├── ic_launcher.webp              [192×192px]
│   └── ic_launcher_round.webp
└── mipmap-anydpi-v26/
    ├── ic_launcher.xml               [Adaptive Icon]
    └── ic_launcher_round.xml
```

### AndroidManifest.xml
```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ...>
</application>
```

---

## 📊 Comparativa: Logo Original vs. Mejorado

| Aspecto | Original | Mejorado ✨ |
|--------|----------|-----------|
| Suavidad de la onda | Lineal, angular | Curvas Bézier, armónica |
| Gradiente | Uniforme | Sutil (claro→oscuro) |
| Silueta | No | Sí (cabeza sutil) |
| Profesionalismo | Bueno | Excelente |
| Calidez | Buena | Superior |
| Escalabilidad | Perfecta | Perfecta |

---

## 🚀 Próximos Pasos

1. ✅ Validar logo en app (compilar proyecto)
2. ✅ Probar en diferentes tamaños de pantalla
3. ✅ Obtener feedback de usuarios
4. 📝 Crear guía de marca completa
5. 🎨 Desarrollar íconos secundarios con mismo estilo
6. 📱 Optimizar WebP para reducir tamaño de app

---

## 📝 Notas de Diseño

- **Silueta de cabeza:** Muy sutil (8% opacidad) para no distraer
- **Línea base de referencia:** Helps mantener equilibrio visual
- **Pico principal en #6D4FA8:** Énfasis en el latido/momento de mayor importancia
- **Ondulaciones laterales:** Representan el continuo monitoreo EEG

---

## 📞 Contacto & Soporte

Para cambios o mejoras, referirse a:
- Archivo principal: `assets/mindtrack_logo_main.svg`
- Foreground Android: `app/src/main/res/drawable/ic_launcher_foreground.xml`

---

**Versión:** 1.0  
**Fecha:** Junio 2026  
**Estado:** ✅ Completo y Funcional

