package com.deeplake.dweapon.util.NBTStrDef;

import com.deeplake.dweapon.DWeapons;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class IDLGeneral {
    //server side dont have this constructor.
    public static AxisAlignedBB ServerAABB(Vec3d from, Vec3d to)
    {
        return new AxisAlignedBB(from.x, from.y, from.z, to.x, to.y, to.z);
    }
}
