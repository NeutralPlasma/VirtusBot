package com.neutralplasma.virtusbot.utils

import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage

enum class Resizer {
    NEAREST_NEIGHBOR {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            return commonResize(source, width, height,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        }
    },
    BILINEAR {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            return commonResize(source, width, height,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        }
    },
    BICUBIC {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            return commonResize(source, width, height,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        }
    },
    PROGRESSIVE_BILINEAR {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            return progressiveResize(source, width, height,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        }
    },
    PROGRESSIVE_BICUBIC {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            return progressiveResize(source, width, height,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        }
    },
    AVERAGE {
        override fun resize(source: BufferedImage,
                            width: Int, height: Int): BufferedImage {
            val img2 = source.getScaledInstance(width, height,
                    Image.SCALE_AREA_AVERAGING)
            val img = BufferedImage(width, height,
                    source.type)
            val g = img.createGraphics()
            try {
                g.drawImage(img2, 0, 0, width, height, null)
            } finally {
                g.dispose()
            }
            return img
        }
    };

    abstract fun resize(source: BufferedImage,
                        width: Int, height: Int): BufferedImage

    companion object {
        private fun progressiveResize(source: BufferedImage,
                                      width: Int, height: Int, hint: Any): BufferedImage {
            var w = Math.max(source.width / 2, width)
            var h = Math.max(source.height / 2, height)
            var img = commonResize(source, w, h, hint)
            while (w != width || h != height) {
                val prev = img
                w = Math.max(w / 2, width)
                h = Math.max(h / 2, height)
                img = commonResize(prev, w, h, hint)
                prev.flush()
            }
            return img
        }

        private fun commonResize(source: BufferedImage,
                                 width: Int, height: Int, hint: Any): BufferedImage {
            val img = BufferedImage(width, height,
                    source.type)
            val g = img.createGraphics()
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint)
                g.drawImage(source, 0, 0, width, height, null)
            } finally {
                g.dispose()
            }
            return img
        }
    }
}