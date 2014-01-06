package flaxbeard.thaumicexploration.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJarFillable;
import cpw.mods.fml.common.network.PacketDispatcher;
import flaxbeard.thaumicexploration.ThaumicExploration;
import flaxbeard.thaumicexploration.data.TXWorldData;
import flaxbeard.thaumicexploration.tile.TileEntityBoundChest;
import flaxbeard.thaumicexploration.tile.TileEntityBoundJar;

public class TXEventHandler {
	public TXEventHandler() {
		//System.out.println("TEST123");
	}
	
	@ForgeSubscribe
	public void handleWorldLoad(WorldEvent.Load event) {
		TXWorldData.get(event.world);
	}
	

	@ForgeSubscribe
	public void handleMobDrop(LivingDropsEvent event) {
		if (event.source == DamageSourceTX.soulCrucible) {
			event.setCanceled(true);
		}
	}
	
	@ForgeSubscribe
	public void handleItemUse(PlayerInteractEvent event) {
		byte type = 0;
		
		if (event.entityPlayer.worldObj.blockExists(event.x, event.y, event.z)) {
			//System.out.println(event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) + " " + ThaumicExploration.boundChest.blockID);
			if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == Block.chest.blockID) {

				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSeal.itemID) {
						type = 1;
					}
					else if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSealLinked.itemID) {
						type = 2;
					}
				}
			}
			else if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ThaumicExploration.boundChest.blockID) {
				//System.out.println("bound chest");
				World world = event.entityPlayer.worldObj;
				//System.out.println(event.entityPlayer.worldObj.isRemote + ItemBlankSeal.itemNames[((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).getSealColor()]);
				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.chestSeal.itemID ) {
						int color = ((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).clientColor;		
						type = 3;
						if (15-(event.entityPlayer.inventory.getCurrentItem().getItemDamage()) == color) {
							int nextID = ((TileEntityBoundChest) world.getBlockTileEntity(event.x, event.y, event.z)).id;
							ItemStack linkedSeal = new ItemStack(ThaumicExploration.chestSealLinked.itemID, 1, event.entityPlayer.inventory.getCurrentItem().getItemDamage());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setInteger("ID", nextID);
							tag.setInteger("x", event.x);
							tag.setInteger("y", event.y);
							tag.setInteger("z", event.z);
							tag.setInteger("dim", world.provider.dimensionId);
							linkedSeal.setTagCompound(tag);

							event.entityPlayer.inventory.addItemStackToInventory(linkedSeal);
							if (!event.entityPlayer.capabilities.isCreativeMode)
								event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
						}
						event.setCanceled(true);
					}
				}
			}
		
		}


		if (event.entityPlayer.worldObj.blockExists(event.x, event.y, event.z)) {
			if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ConfigBlocks.blockJar.blockID && event.entityPlayer.worldObj.getBlockMetadata(event.x, event.y, event.z) == 0) {
				if (event.entityPlayer.inventory.getCurrentItem() != null && ((TileJarFillable)event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z)).aspectFilter == null && ((TileJarFillable)event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z)).amount == 0){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSeal.itemID) {
						type = 4;
					}
					else if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSealLinked.itemID) {
						type = 5;
					}
				}
			}
			else if (event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == ThaumicExploration.boundJar.blockID) {
				World world = event.entityPlayer.worldObj;
				if (event.entityPlayer.inventory.getCurrentItem() != null){ 
					if (event.entityPlayer.inventory.getCurrentItem().itemID == ThaumicExploration.jarSeal.itemID ) {
						int color = ((TileEntityBoundJar) world.getBlockTileEntity(event.x, event.y, event.z)).getSealColor();		
						type = 6;
						if (15-(event.entityPlayer.inventory.getCurrentItem().getItemDamage()) == color) {
							int nextID = ((TileEntityBoundJar) world.getBlockTileEntity(event.x, event.y, event.z)).id;
							ItemStack linkedSeal = new ItemStack(ThaumicExploration.jarSealLinked.itemID, 1, event.entityPlayer.inventory.getCurrentItem().getItemDamage());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setInteger("ID", nextID);
							tag.setInteger("x", event.x);
							tag.setInteger("y", event.y);
							tag.setInteger("z", event.z);
							tag.setInteger("dim", world.provider.dimensionId);
							linkedSeal.setTagCompound(tag);

							event.entityPlayer.inventory.addItemStackToInventory(linkedSeal);
							if (!event.entityPlayer.capabilities.isCreativeMode)
								event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
						}
						event.setCanceled(true);
					}
				}
			}
		}
		
		
		
		if (event.entityPlayer.worldObj.isRemote && type > 0) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	        DataOutputStream outputStream = new DataOutputStream(bos);
	
	        try
	        {
	            outputStream.writeByte(1);
	            outputStream.writeInt(event.entityPlayer.worldObj.provider.dimensionId);
	            outputStream.writeInt(event.x);
	            outputStream.writeInt(event.y);
	            outputStream.writeInt(event.z);
	            outputStream.writeByte(type);
	            outputStream.writeInt( event.entityPlayer.entityId);
	           
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
	
	        Packet250CustomPayload packet = new Packet250CustomPayload();
	        packet.channel = "tExploration";
	        packet.data = bos.toByteArray();
	        packet.length = bos.size();
	        //PacketDispatcher.sendPacketToServer(packet);
	        PacketDispatcher.sendPacketToServer(packet);
	        //System.out.println("sent");
		}

		
	}
	
}
