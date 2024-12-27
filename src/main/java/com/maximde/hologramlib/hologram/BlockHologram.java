package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import org.joml.Vector3f;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class BlockHologram extends Hologram<BlockHologram> {

    @Getter @Setter
    @Accessors(chain = true)
    protected int block = 0;

    @Getter @Setter
    @Accessors(chain = true)
    protected boolean onFire = false;

    @Getter @Setter
    @Accessors(chain = true)
    protected boolean glowing = false;

    @Getter @Setter
    @Accessors(chain = true)
    protected int glowColor = Color.YELLOW.getRGB();


    public BlockHologram(String id, RenderMode renderMode) {
        super(id, renderMode, EntityTypes.BLOCK_DISPLAY);
    }

    public BlockHologram(String id, EntityType entityType) {
        super(id, entityType);
    }

    @Override
    protected WrapperPlayServerEntityMetadata createMeta() {
        BlockDisplayMeta meta = (BlockDisplayMeta) EntityMeta.createMeta(super.entityID, EntityTypes.BLOCK_DISPLAY);
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(super.toVector3f(this.translation));
        meta.setScale(super.toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setViewRange((float) this.viewRange);
        meta.setBlockId(this.block);
        meta.setOnFire(this.onFire);
        meta.setGlowing(this.glowing);
        meta.setGlowColorOverride(this.glowColor);
        return meta.createPacket();
    }

    protected BlockHologram copy() {
        int randomNumber = ThreadLocalRandom.current().nextInt(100000);
        return this.copy(this.id + "_copy_" + randomNumber);
    }

    @Override
    protected BlockHologram copy(String id) {
        BlockHologram copy = new BlockHologram(id, this.renderMode);
        copy.block = this.block;
        copy.glowColor = this.glowColor;
        copy.glowing = this.glowing;
        copy.onFire = this.onFire;
        copy.scale = new Vector3f(this.scale);
        copy.translation = new Vector3f(this.translation);
        copy.rightRotation = new Quaternion4f(this.rightRotation.getX(), this.rightRotation.getY(),
                this.rightRotation.getZ(), this.rightRotation.getW());
        copy.leftRotation = new Quaternion4f(this.leftRotation.getX(), this.leftRotation.getY(),
                this.leftRotation.getZ(), this.leftRotation.getW());
        copy.billboard = this.billboard;
        copy.interpolationDurationRotation = this.interpolationDurationRotation;
        copy.interpolationDurationTransformation = this.interpolationDurationTransformation;
        copy.viewRange = this.viewRange;
        copy.updateTaskPeriod = this.updateTaskPeriod;
        copy.nearbyEntityScanningDistance = this.nearbyEntityScanningDistance;
        return copy;
    }
}