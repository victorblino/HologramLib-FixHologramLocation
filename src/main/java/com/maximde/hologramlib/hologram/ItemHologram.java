package com.maximde.hologramlib.hologram;


import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;


import java.awt.*;


@Getter
public class ItemHologram extends Hologram<ItemHologram> {

    @Setter
    @Accessors(chain = true)
    protected ItemDisplayMeta.DisplayType displayType = ItemDisplayMeta.DisplayType.FIXED;

    @Setter
    @Accessors(chain = true)
    protected boolean onFire = false;

    @Setter
    @Accessors(chain = true)
    protected ItemStack item = new ItemStack.Builder()
            .type(ItemTypes.AIR).build();

    @Setter
    @Accessors(chain = true)
    protected boolean glowing = false;

    @Accessors(chain = true)
    protected int glowColor = 0;

    public ItemHologram(String id, RenderMode renderMode) {
        super(id, renderMode, EntityTypes.ITEM_DISPLAY);
    }

    public ItemHologram(String id) {
        this(id, RenderMode.NEARBY);
    }

    public void setGlowColor(Color color) {
        int rgb = color.getRGB();
        this.glowColor = ((rgb & 0xFF0000) >> 16) |
                (rgb & 0x00FF00) |
                ((rgb & 0x0000FF) << 16);
    }


    @Override
    protected WrapperPlayServerEntityMetadata createMeta() {
        ItemDisplayMeta meta = (ItemDisplayMeta) EntityMeta.createMeta(super.entityID, EntityTypes.ITEM_DISPLAY);
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(super.toVector3f(this.translation));
        meta.setScale(super.toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setViewRange((float) this.viewRange);
        meta.setDisplayType(this.displayType);
        meta.setOnFire(this.onFire);
        meta.setItem(this.item);
        meta.setGlowing(this.glowing);
        meta.setGlowColorOverride(this.glowColor);
        return meta.createPacket();
    }


    @Override
    protected ItemHologram copy() {
        //TODO
        return null;
    }

    @Override
    protected ItemHologram copy(String id) {
        //TODO
        return null;
    }
}