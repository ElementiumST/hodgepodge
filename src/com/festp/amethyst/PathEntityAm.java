package com.festp.amethyst;

import com.festp.Utils;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.PathEntity;
import net.minecraft.server.v1_13_R2.PathPoint;
import net.minecraft.server.v1_13_R2.Vec3D;

public class PathEntityAm extends PathEntity {

	public final PathPoint[] a;
	public int b;
	public int c;

    public PathEntityAm(PathPoint[] apathpoint) {
    	super(apathpoint);
        this.a = apathpoint;
        this.c = apathpoint.length;
    }

    public PathEntityAm(PathEntity a2) {
    	super((PathPoint[]) Utils.getPrivateField("a", PathEntity.class, a2));
    	PathPoint[] apathpoint = (PathPoint[]) Utils.getPrivateField("a", PathEntity.class, a2);
        this.a = apathpoint;
        this.c = apathpoint.length;
	}

	public void a() {
        ++this.b;
    }

    public boolean b() {
        return this.b >= this.c;
    }

    public PathPoint c() {
        return this.c > 0 ? this.a[this.c - 1] : null;
    }

    public PathPoint a(int i) {
        return this.a[i];
    }

    public int d() {
        return this.c;
    }

    public void b(int i) {
        this.c = i;
    }

    public int e() {
        return this.b;
    }

    public void c(int i) {
        this.b = i;
    }

    public Vec3D a(Entity entity, int i) {
        double d0 = (double) this.a[i].a + (double) ((int) (entity.width + 1.0F)) * 0.5D;
        double d1 = (double) this.a[i].b;
        double d2 = (double) this.a[i].c + (double) ((int) (entity.width + 1.0F)) * 0.5D;

        return new Vec3D(d0, d1, d2);
    }

    public Vec3D a(Entity entity) {
        return this.a(entity, this.b);
    }

    public boolean a(PathEntityAm pathentity) {
        if (pathentity == null) {
            return false;
        } else if (pathentity.a.length != this.a.length) {
            return false;
        } else {
            for (int i = 0; i < this.a.length; ++i) {
                if (this.a[i].a != pathentity.a[i].a || this.a[i].b != pathentity.a[i].b || this.a[i].c != pathentity.a[i].c) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean b(Vec3D vec3d) {
        PathPoint pathpoint = this.c();

        return pathpoint == null ? false : pathpoint.a == (int) vec3d.x && pathpoint.c == (int) vec3d.z;
    }

}
