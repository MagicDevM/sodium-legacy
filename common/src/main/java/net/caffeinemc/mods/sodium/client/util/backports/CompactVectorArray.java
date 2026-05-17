package net.caffeinemc.mods.sodium.client.util.backports.CompactVectorArray;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CompactVectorArray {
   private final float[] contents;

   public CompactVectorArray(int size) {
      this.contents = new float[3 * size];
   }

   public int size() {
      return this.contents.length / 3;
   }

   public void set(int arg0, Vector3fc arg1) {
      this.set(arg0, arg1.x(), arg1.y(), arg1.z());
   }

   public void set(int arg0, float arg1, float arg2, float arg3) {
      this.contents[3 * arg0 + 0] = arg1;
      this.contents[3 * arg0 + 1] = arg2;
      this.contents[3 * arg0 + 2] = arg3;
   }

   public Vector3f get(int arg0, Vector3f arg1) {
      return arg1.set(this.contents[3 * arg0 + 0], this.contents[3 * arg0 + 1], this.contents[3 * arg0 + 2]);
   }

   public float getX(int arg0) {
      return this.contents[3 * arg0 + 0];
   }

   public float getY(int arg0) {
      return this.contents[3 * arg0 + 1];
   }

   public float getZ(int arg0) {
      return this.contents[3 * arg0 + 1];
   }
}
