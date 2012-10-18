package com.dynamo.cr.parted.nodes;

import java.nio.FloatBuffer;
import java.util.EnumSet;

import javax.media.opengl.GL;
import javax.vecmath.Point3d;

import com.dynamo.cr.sceneed.core.INodeRenderer;
import com.dynamo.cr.sceneed.core.RenderContext;
import com.dynamo.cr.sceneed.core.RenderContext.Pass;
import com.dynamo.cr.sceneed.core.RenderData;
import com.dynamo.cr.sceneed.ui.ManipulatorRendererUtil;
import com.dynamo.cr.sceneed.ui.RenderUtil;

public class RadialRenderer implements INodeRenderer<RadialNode> {

    private static final float[] color = new float[] { 1, 1, 1, 1 };
    private static final EnumSet<Pass> passes = EnumSet.of(Pass.OUTLINE, Pass.SELECTION);
    private FloatBuffer circle;

    public RadialRenderer() {
        circle = RenderUtil.createDashedCircle(64);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setup(RenderContext renderContext, RadialNode node) {
        if (passes.contains(renderContext.getPass())) {
            renderContext.add(this, node, new Point3d(), null);
            if (renderContext.isSelected(node)) {
                renderContext.add(this, node, new Point3d(), circle);
            }
        }
    }

    @Override
    public void render(RenderContext renderContext, RadialNode node,
            RenderData<RadialNode> renderData) {

        GL gl = renderContext.getGL();
        double factor = ManipulatorRendererUtil.getScaleFactor(node, renderContext.getRenderView());
        float[] color = renderContext.selectColor(node, RadialRenderer.color);
        gl.glColor4fv(color, 0);
        double magnitude = node.getMagnitude();

        if (renderData.getUserData() == null) {
            int n = 8;
            for (int i = 0; i < n; ++i) {
                gl.glPushMatrix();
                double a = 360.0 * i / (double) n;

                gl.glRotated(a, 0, 0, 1);

                if (magnitude < 0) {
                    gl.glTranslated(30 / factor, 0, 0);
                    gl.glRotated(180, 0, 0, 1);
                    gl.glTranslated(-30 / factor, 0, 0);
                }

                gl.glTranslated(5.0 / factor, 0, 0);
                drawArrow(gl, factor);
                gl.glPopMatrix();
            }
        } else {
            double r = node.getMaxDistance();
            gl.glPushMatrix();
            gl.glScaled(r, r, 1.0);
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, circle);
            gl.glDrawArrays(GL.GL_LINES, 0, circle.limit() / 3);
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
            gl.glPopMatrix();
        }
    }

    private void drawArrow(GL gl, double factor) {
        RenderUtil.drawArrow(gl, 0.4 * ManipulatorRendererUtil.BASE_LENGTH / factor,
                                 1.3 * ManipulatorRendererUtil.BASE_HEAD_RADIUS / factor,
                                 0.2 * ManipulatorRendererUtil.BASE_THICKNESS / factor,
                                 0.5 * ManipulatorRendererUtil.BASE_HEAD_RADIUS / factor);
    }


}
