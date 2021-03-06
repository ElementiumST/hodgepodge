package com.festp.remain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.TreeSpecies;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Turtle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.festp.Utils;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

//to AbstractHorse and Donkeys

//entity id had replaced tome id
public class SummonerTome implements Listener {
	enum Tome {/*UNDEFINED, */MINECART, BOAT, HORSE, CUSTOM_HORSE, ALL, CUSTOM_ALL};
	private static final String MAIN_SEP = ";";
	private static final String FIELD_SEP = "|";
	private static final String KEYVAL_SEP = ":";
	private static final String MAIN_SEP_split = ";";
	private static final String FIELD_SEP_split = "\\|";
	private static final String KEYVAL_SEP_split = "\\:";
	
	public static final String lore_eng_minecart_tome = "Summons minecart";
	public static final String lore_eng_boat_tome = "Summons boat";
	public static final String lore_eng_horse_tome = "Summons horse";
	public static final String lore_eng_custom_horse_tome = "Summons custom horse";
	public static final String lore_eng_all_tome = "Summons minecart, boat or horse";
	public static final String lore_eng_custom_all_tome =  "Summons minecart, boat or custom horse";
	public static final String name_eng_minecart_tome = "Minecart tome";
	public static final String name_eng_boat_tome = "Boat tome";
	public static final String name_eng_horse_tome = "Horse tome";
	public static final String name_eng_custom_horse_tome = "Advanced horse tome";
	public static final String name_eng_all_tome = "United tome";
	public static final String name_eng_custom_all_tome =  "Advanced united tome";
	
	public static final double searching_radius_minecart_tome = 1.5;
	public static final double searching_radius_boat_tome = 2.5;
	
	private static JavaPlugin plugin;
	private List<AbstractHorse> save_horse = new ArrayList<>();
	private List<Player> save_player = new ArrayList<>();
	
	public SummonerTome(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void addSavingTome(AbstractHorse horse, Player p) {
		save_horse.add(horse);
		save_player.add(p);
	}
	
	public void onTick() {
		for(int i = save_horse.size()-1; i >= 0; i--) {
			process_custom_horse(save_horse.get(i), save_player.get(i));
			save_horse.remove(i);
			save_player.remove(i);
		}
	}

	public static void addTomeCrafts(JavaPlugin plugin) {
		Server server = plugin.getServer();
		
		//minecart tome - book, 4 xp bottle and 4 minecarts
    	NamespacedKey key_minecart = new NamespacedKey(plugin, "minecart_tome");
    	ItemStack minecart_book = new ItemStack(Material.ENCHANTED_BOOK);
    	minecart_book = setTome(minecart_book, 'm', " ");
    	ItemMeta mc_meta = minecart_book.getItemMeta();
    	mc_meta.setDisplayName(name_eng_minecart_tome);
    	mc_meta.setLore(Arrays.asList(lore_eng_minecart_tome));
    	minecart_book.setItemMeta(mc_meta);
    	ShapelessRecipe minecart_tome = new ShapelessRecipe(key_minecart, minecart_book);
    	minecart_tome.addIngredient(1, Material.BOOK);
    	minecart_tome.addIngredient(4, Material.EXPERIENCE_BOTTLE);
    	minecart_tome.addIngredient(4, Material.MINECART);
    	server.addRecipe(minecart_tome);
		
		//boat tome - book, 2 xp bottle and 6 colors boats
    	//all tomes with boats can be customized by all the 6 boat types
    	NamespacedKey key_boat = new NamespacedKey(plugin, "boat_tome");
    	ItemStack boat_book = new ItemStack(Material.ENCHANTED_BOOK);
    	boat_book = setTome(boat_book, 'b', "o");
    	ItemMeta boat_meta = boat_book.getItemMeta();
    	boat_meta.setDisplayName(name_eng_boat_tome);
    	boat_meta.setLore(Arrays.asList(lore_eng_boat_tome));
    	boat_book.setItemMeta(boat_meta);
    	ShapelessRecipe boat_tome = new ShapelessRecipe(key_boat, boat_book);
    	boat_tome.addIngredient(1, Material.BOOK);
    	boat_tome.addIngredient(2, Material.EXPERIENCE_BOTTLE);
    	boat_tome.addIngredient(1, Material.ACACIA_BOAT);
    	boat_tome.addIngredient(1, Material.BIRCH_BOAT);
    	boat_tome.addIngredient(1, Material.DARK_OAK_BOAT);
    	boat_tome.addIngredient(1, Material.JUNGLE_BOAT);
    	boat_tome.addIngredient(1, Material.OAK_BOAT);
    	boat_tome.addIngredient(1, Material.SPRUCE_BOAT);
    	server.addRecipe(boat_tome);
    	
		//horse tome - book, 2 xp bottles, 4 saddles, 2 (leads?)
    	//unwearable armor, untakeble saddle
    	NamespacedKey key_horse = new NamespacedKey(plugin, "horse_tome");
    	ItemStack horse_book = new ItemStack(Material.ENCHANTED_BOOK);
    	horse_book = setTome(horse_book, 'h', " ");
    	ItemMeta horse_meta = horse_book.getItemMeta();
    	horse_meta.setDisplayName(name_eng_horse_tome);
    	horse_meta.setLore(Arrays.asList(lore_eng_horse_tome));
    	horse_book.setItemMeta(horse_meta);
    	ShapelessRecipe horse_tome = new ShapelessRecipe(key_horse, horse_book);
    	horse_tome.addIngredient(1, Material.BOOK);
    	horse_tome.addIngredient(2, Material.EXPERIENCE_BOTTLE);
    	horse_tome.addIngredient(2, Material.LEAD);
    	horse_tome.addIngredient(2, Material.SADDLE);
    	horse_tome.addIngredient(1, Material.APPLE);
    	horse_tome.addIngredient(1, Material.GOLDEN_APPLE);
    	server.addRecipe(horse_tome);
    	
		//custom horse tome - horse tome, 4 xp bottles, jump potion, speed potion, instheal potion, label
    	//in craft events because of Horse tome and potions
    	NamespacedKey key_custom_horse = new NamespacedKey(plugin, "custom_horse_tome");
    	ItemStack custom_horse_book = new ItemStack(Material.ENCHANTED_BOOK);
    	custom_horse_book = setTome(custom_horse_book, 'H', " ");
    	ItemMeta custom_horse_meta = custom_horse_book.getItemMeta();
    	custom_horse_meta.setDisplayName(name_eng_custom_horse_tome);
    	custom_horse_meta.setLore(Arrays.asList(lore_eng_custom_horse_tome));
    	custom_horse_book.setItemMeta(custom_horse_meta);
    	ShapelessRecipe custom_horse_tome = new ShapelessRecipe(key_custom_horse, custom_horse_book);
    	custom_horse_tome.addIngredient(1, Material.ENCHANTED_BOOK);
    	custom_horse_tome.addIngredient(4, Material.EXPERIENCE_BOTTLE);
    	custom_horse_tome.addIngredient(1, Material.NAME_TAG);
    	custom_horse_tome.addIngredient(1, Material.POTION);
    	custom_horse_tome.addIngredient(1, Material.POTION);
    	custom_horse_tome.addIngredient(1, Material.POTION);
    	server.addRecipe(custom_horse_tome);
		
		//united tome - horse, minecart, boat tomes, slime block, 5 xp bottles
    	NamespacedKey key_all = new NamespacedKey(plugin, "all_tome");
    	ItemStack all_book = new ItemStack(Material.ENCHANTED_BOOK);
    	all_book = setTome(all_book, 'a', "o");
    	ItemMeta all_meta = all_book.getItemMeta();
    	all_meta.setDisplayName(name_eng_all_tome); //sovmeshat + perenesti nazvaniya naverh
    	all_meta.setLore(Arrays.asList(lore_eng_all_tome));
    	all_book.setItemMeta(all_meta);
    	ShapelessRecipe all_tome = new ShapelessRecipe(key_all, all_book);
    	all_tome.addIngredient(3, Material.ENCHANTED_BOOK);
    	all_tome.addIngredient(5, Material.EXPERIENCE_BOTTLE);
    	all_tome.addIngredient(1, Material.SLIME_BLOCK);
    	server.addRecipe(all_tome);
    	
    	//united custom tome - slime block, custom horse, minecart, boat tomes, 5 xp bottles
    	//from custom horse, boat and minecart tomes
    	
    	//from united tome
	}
	
	//set horse name and let spigot using custom potions
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		Tome type = whichTome(event.getInventory().getResult());
		if(type == null) return;

		System.out.println("type: "+type);
		//horse/all tome, NAME from nametag and jump, speed and instheal potions
		if(type == Tome.CUSTOM_HORSE) { //custom_horse or custom_all from all
			ItemStack[] matrix = event.getInventory().getMatrix();
			System.out.println("valid type, matrix size: "+matrix.length);
			
			String custom_name = null;
			boolean correct = true, have_ms = false, have_jump = false, have_hp = false;
			Tome new_type = null;
			ItemStack old_tome = null;
			for(int i = 0; i < matrix.length; i++) {
				if(!correct) break;
				if(matrix[i].getType() == Material.NAME_TAG) {
					if(matrix[i].getItemMeta().hasDisplayName()) {
						custom_name = matrix[i].getItemMeta().getDisplayName();
					}
				}
				else if(matrix[i].getType() == Material.POTION) {
					PotionMeta potion = (PotionMeta) matrix[i].getItemMeta();
					List<PotionEffect> effect_list;
					if(potion.hasCustomEffects())
						effect_list = potion.getCustomEffects();
					else
						effect_list = new ArrayList<>();
					System.out.println("PE list: "+effect_list);
					PotionData pd = potion.getBasePotionData();
					PotionEffect pet = pd.getType().getEffectType().createEffect(1, 1);
					effect_list.add(pet);
					for(PotionEffect pe : effect_list) {
						if(pe.getType() == PotionEffectType.SPEED)
							if(have_ms) {
								correct = false;
								break;
							}
							else {
								have_ms = true;
							}
						else if(pe.getType() == PotionEffectType.JUMP)
							if(have_jump) {
								correct = false;
								break;
							}
							else {
								have_jump = true;
							}
						else if(pe.getType() == PotionEffectType.HEAL)
							if(have_hp) {
								correct = false;
								break;
							}
							else {
								have_hp = true;
							}
					}
				}
				else if(matrix[i].getType() == Material.ENCHANTED_BOOK) {
					if(new_type != null) {
						correct = false;
						break;
					}
					old_tome = matrix[i];
					Tome old_type = whichTome(matrix[i]);
					if(old_type == Tome.HORSE) {
						new_type = Tome.CUSTOM_HORSE;
					}
					else if(old_type == Tome.ALL) {
						new_type = Tome.CUSTOM_ALL;
					}
					else {
						correct = false;
						break;
					}
				}
			}
			
			System.out.println("total: "+correct + " " + (new_type != null) + " " + have_hp + " " + have_jump + " " + have_ms);
			if(correct && new_type != null && have_hp && have_jump && have_ms) {
				if(new_type == Tome.CUSTOM_ALL) {
					TreeSpecies wood_type = get_boat_type(old_tome);
			    	ItemStack custom_all2_book = setTome(old_tome, 'A', "o");
			    	custom_all2_book = set_boat_type(custom_all2_book, wood_type);
			    	ItemMeta custom_all2_meta = custom_all2_book.getItemMeta();
			    	custom_all2_meta.setDisplayName(name_eng_custom_all_tome);
			    	custom_all2_meta.setLore(Arrays.asList(lore_eng_custom_all_tome));
			    	custom_all2_book.setItemMeta(custom_all2_meta);
			    	event.getInventory().setResult(custom_all2_book);
				}
				if(custom_name != null) {
					ItemStack tome = event.getInventory().getResult();
					tome.getItemMeta().setDisplayName(custom_name);
					event.getInventory().setResult(tome);
				}
		    	return;
			}
			else
				event.getInventory().setResult(null);
		}
		//all three books
		if(type == Tome.ALL || type == Tome.CUSTOM_ALL) {
			ItemStack[] matrix = event.getInventory().getMatrix();
			
			boolean correct = true, have_mc = false, have_boat = false; int have_horse = 0;
			Tome new_type = null;
			ItemStack old_boat = null;
			ItemStack old_custom_horse = null;
			for(int i = 0; i < matrix.length; i++) {
				if(!correct) break;
				else if(matrix[i].getType() == Material.ENCHANTED_BOOK) {
					Tome old_type = whichTome(matrix[i]);
					if(old_type == Tome.MINECART) {
						if(have_mc) {
							correct = false;
							break;
						}
						have_mc = true;
					}
					else if(old_type == Tome.BOAT) {
						if(have_boat) {
							correct = false;
							break;
						}
						have_boat = true;
						old_boat = matrix[i];
					}
					else if(old_type == Tome.HORSE) {
						if(have_horse > 0) {
							correct = false;
							break;
						}
						have_horse = 1;
						new_type = Tome.ALL;
					}
					else if(old_type == Tome.CUSTOM_HORSE) {
						if(have_horse > 0) {
							correct = false;
							break;
						}
						have_horse = 2;
						old_custom_horse = matrix[i];
						new_type = Tome.CUSTOM_ALL;
					}
					else {
						correct = false;
						break;
					}
				}
			}
			
			if(correct && new_type != null && have_mc && have_boat && have_horse > 0) {
				if(new_type == Tome.ALL) {
					TreeSpecies wood_type = get_boat_type(old_boat);
			    	ItemStack all_book = event.getInventory().getResult();
			    	all_book = set_boat_type(all_book, wood_type);
			    	event.getInventory().setResult(all_book);
				}
				if(new_type == Tome.CUSTOM_ALL) {
					TreeSpecies wood_type = get_boat_type(old_boat);
			    	ItemStack custom_all_book = new ItemStack(Material.ENCHANTED_BOOK);
			    	custom_all_book = setTome(event.getInventory().getResult(), 'A', "o");
			    	custom_all_book = set_boat_type(custom_all_book, wood_type);
			    	custom_all_book = copy_horse_data(custom_all_book, get_horse_data(old_custom_horse));
			    	ItemMeta custom_all_meta = custom_all_book.getItemMeta();
			    	if(old_custom_horse.getItemMeta().hasDisplayName())
				    	custom_all_meta.setDisplayName(old_custom_horse.getItemMeta().getDisplayName());
			    	else
			    		custom_all_meta.setDisplayName(name_eng_custom_all_tome);
			    	custom_all_meta.setLore(Arrays.asList(lore_eng_custom_all_tome));
			    	custom_all_book.setItemMeta(custom_all_meta);
			    	event.getInventory().setResult(custom_all_book);
				}
		    	return;
			}
			else
				event.getInventory().setResult(null);
		}
	}

	//set boat type
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Tome type = whichTome(event.getInventory().getResult());
		if(type == null) return;
		
		if(type == Tome.BOAT) {
			Material wood_type = event.getInventory().getMatrix()[4].getType(); //central cell
			if(wood_type == Material.ACACIA_BOAT)
				event.getInventory().setResult(setTome(event.getInventory().getResult(), 'b', "a"));
			else if(wood_type == Material.BIRCH_BOAT)
				event.getInventory().setResult(setTome(event.getInventory().getResult(), 'b', "b"));
			else if(wood_type == Material.DARK_OAK_BOAT)
				event.getInventory().setResult(setTome(event.getInventory().getResult(), 'b', "d"));
			else if(wood_type == Material.JUNGLE_BOAT)
				event.getInventory().setResult(setTome(event.getInventory().getResult(), 'b', "j"));
			else if(wood_type == Material.SPRUCE_BOAT)
				event.getInventory().setResult(setTome(event.getInventory().getResult(), 'b', "s"));
		}
		//wood type from boat/all tome and custom horse from custom_horse
		/*else if(type == Tome.ALL || type == Tome.CUSTOM_ALL) {
			
		}*/
	}
	
	//Customization
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) { //PlayerInteractAtEntityEvent
		if(wasSummoned(event.getRightClicked())) return;
		
		boolean main_hand = true;
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null) { //dye only on coloring click
			main_hand = false;
			item = event.getPlayer().getInventory().getItemInOffHand();
			if(item == null)
				return;
		}

		Tome type = whichTome(item);
		if(type == null) return;
		if(hasSummoned(item)) return;
		
		Entity entity = event.getRightClicked();
		if(entity instanceof Boat && (type == Tome.BOAT || type == Tome.ALL || type == Tome.CUSTOM_ALL)
				&& get_boat_type(item) != ((Boat)entity).getWoodType()) {
			TreeSpecies prev_type = get_boat_type(item);
			if(main_hand)
				event.getPlayer().getInventory().setItemInMainHand(set_boat_type(item, ((Boat)entity).getWoodType()));
			else
				event.getPlayer().getInventory().setItemInOffHand(set_boat_type(item, ((Boat)entity).getWoodType()));
			((Boat)entity).setWoodType(prev_type);
			event.setCancelled(true);
		}
		else if(entity instanceof Horse && (type == Tome.CUSTOM_HORSE || type == Tome.CUSTOM_ALL)) {
			Horse horse = (Horse)entity;
			if(horse.getInventory().getSaddle() != null) {
				String new_data = get_horse_data(horse);
				set_horse_data(horse, get_horse_data(item));
				if(main_hand)
					event.getPlayer().getInventory().setItemInMainHand(copy_horse_data(item, new_data));
				else
					event.getPlayer().getInventory().setItemInOffHand(copy_horse_data(item, new_data));
				event.setCancelled(true);
			}
		}
	}
	
	//Summoning
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getPlayer().isInsideVehicle() || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

		boolean main_hand = true;
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(item == null || Utils.is_dye(item.getType())) { //dye only on coloring click
			main_hand = false;
			item = event.getPlayer().getInventory().getItemInOffHand();
			if(item == null || Utils.is_dye(item.getType()))
				return;
		}
		Tome type = whichTome(item);
		if(type == null) return;
		
		if(!event.hasBlock() || !Utils.isInteractable(event.getClickedBlock().getType())) {
			Location player_loc = event.getPlayer().getLocation();
			if(type == Tome.MINECART) {
				Location l = findForMinecart(player_loc, searching_radius_minecart_tome);
				if(l == null) return;
				summonMinecart(l, event.getPlayer(), main_hand);
			}
			else if(type == Tome.BOAT) {
				Location l = findForBoat(player_loc, searching_radius_boat_tome);
				if(l == null) return;
				Entity summoned = summonBoat(l, event.getPlayer(), main_hand, get_boat_type(item));
			}
			else if(type == Tome.HORSE) {
				Location l = Utils.find_horse_space(player_loc);
				if(l == null) return;
				l.setY(player_loc.getY());
				Entity summoned = summonHorse(l, event.getPlayer(), main_hand);
			}
			else if(type == Tome.CUSTOM_HORSE) {
				Location l = Utils.find_horse_space(player_loc);
				if(l == null) return;
				l.setY(player_loc.getY());
				Horse custom_horse = summonCustomHorse(l, event.getPlayer(), main_hand);
				if(item.getItemMeta().hasDisplayName() && !name_eng_custom_horse_tome.contains(item.getItemMeta().getDisplayName())) {
					custom_horse.setCustomName(item.getItemMeta().getDisplayName());
				}
				String horse_data = get_horse_data(item);
				if(horse_data == "")
					if(main_hand) event.getPlayer().getInventory().setItemInMainHand(copy_horse_data(item, get_horse_data(custom_horse)));
					else event.getPlayer().getInventory().setItemInOffHand(copy_horse_data(item, get_horse_data(custom_horse)));
				else
					set_horse_data(custom_horse, horse_data);
				
				item = setHasSummoned(item, custom_horse.getUniqueId());
				if(main_hand) event.getPlayer().getInventory().setItemInMainHand(item);
				else event.getPlayer().getInventory().setItemInOffHand(item);
			}
			else if(type == Tome.ALL || type == Tome.CUSTOM_ALL) {
				Location l_mc = findForMinecart(player_loc, searching_radius_minecart_tome);
				Location l_boat = findForBoat(player_loc, searching_radius_boat_tome);
				if(l_mc != null) {
					if(l_boat != null) {
						if(player_loc.distanceSquared(l_mc) < player_loc.distanceSquared(l_boat)) {
							summonMinecart(l_mc, event.getPlayer(), main_hand);
						}
						else {
							Entity summoned = summonBoat(l_boat, event.getPlayer(), main_hand, get_boat_type(item));
						}
					}
					else {
						summonMinecart(l_mc, event.getPlayer(), main_hand);
					}
				}
				else {
					if(l_boat != null) {
						Entity summoned = summonBoat(l_boat, event.getPlayer(), main_hand, get_boat_type(item));
					}
					else {
						//summonHorse(player_loc, event.getPlayer());
						Location l = Utils.find_horse_space(player_loc);
						if(l == null) return;
						
						if(type == Tome.ALL) {
							Entity summoned = summonHorse(l, event.getPlayer(), main_hand);
						}
						else {
							Horse custom_horse = summonCustomHorse(l, event.getPlayer(), main_hand);
						}
					}
				}
			}
		}
	}
	
	public static Location findForMinecart(Location player_loc, double hor_radius) {
		Location l = Utils.searchBlock(new Material[]
				{Material.RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL},
				player_loc, hor_radius, false);
		
		return l;
	}
	public static Minecart summonMinecart(Location l, Player p, boolean main_hand) {
		Minecart mc = l.getWorld().spawn(l, Minecart.class);
		mc.addPassenger(p);
		//mc.setMetadata("fromtome", new FixedMetadataValue(plugin, true));
		setSummoned(mc);
		
		ItemStack tome;
		if(main_hand) tome = p.getInventory().getItemInMainHand();
		else tome = p.getInventory().getItemInOffHand();
		tome = setHasSummoned(tome, mc.getUniqueId());
		if(main_hand) p.getInventory().setItemInMainHand(tome);
		else p.getInventory().setItemInOffHand(tome);
		return mc;
	}
	
	public static Location findForBoat(Location player_loc, double hor_radius) { //watered blocks
		player_loc.add(0, -1, 0);
		Location l = Utils.searchBlock22Platform(new Material[]
				{Material.WATER, Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.FROSTED_ICE, Material.SEA_PICKLE, Material.SEAGRASS, Material.TALL_SEAGRASS},
				player_loc, hor_radius, true);
		player_loc.add(0, 1, 0);
		
		return l;
	}
	public static Boat summonBoat(Location l, Player p, boolean main_hand, TreeSpecies type) {
		l.add(0, 1, 0);
		l.setPitch(p.getLocation().getPitch());
		l.setYaw(p.getLocation().getYaw());
		Boat boat = l.getWorld().spawn(l, Boat.class);
		boat.setWoodType(type);
		boat.addPassenger(p);
		setSummoned(boat);
		
		ItemStack tome;
		if(main_hand) tome = p.getInventory().getItemInMainHand();
		else tome = p.getInventory().getItemInOffHand();
		tome = setHasSummoned(tome, boat.getUniqueId());
		if(main_hand) p.getInventory().setItemInMainHand(tome);
		else p.getInventory().setItemInOffHand(tome);
		return boat;
	}

	public static Horse summonHorse(Location l, Player p, boolean main_hand) { //Donkey
		Horse horse = l.getWorld().spawn(l, Horse.class);
		horse.setTamed(true);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setOwner(p);
		horse.addPassenger(p);
		setSummoned(horse);
		
		ItemStack tome;
		if(main_hand) tome = p.getInventory().getItemInMainHand();
		else tome = p.getInventory().getItemInOffHand();
		tome = setHasSummoned(tome, horse.getUniqueId());
		if(main_hand) p.getInventory().setItemInMainHand(tome);
		else p.getInventory().setItemInOffHand(tome);
		return horse;
	}
	public static Horse summonCustomHorse(Location l, Player p, boolean main_hand) { //Donkey
		Horse horse = l.getWorld().spawn(l, Horse.class);
		horse.setTamed(true);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setOwner(p);
		horse.addPassenger(p);
		setSummoned(horse);
		setCustomHorse(horse);

		ItemStack tome;
		if(main_hand) tome = p.getInventory().getItemInMainHand();
		else tome = p.getInventory().getItemInOffHand();
		tome = setHasSummoned(tome, horse.getUniqueId());
		if(tome.getItemMeta().hasDisplayName() && !(name_eng_custom_horse_tome.contains(tome.getItemMeta().getDisplayName())
				|| name_eng_custom_all_tome.contains(tome.getItemMeta().getDisplayName()))) {
			horse.setCustomName(tome.getItemMeta().getDisplayName());
		}
		String horse_data = get_horse_data(tome);
		if(horse_data.equals(""))
			tome = copy_horse_data(tome, get_horse_data(horse));
		else
			set_horse_data(horse, horse_data);
		
		if(main_hand) p.getInventory().setItemInMainHand(tome);
		else p.getInventory().setItemInOffHand(tome);
		
		if(!horse.isAdult())
			horse.setAgeLock(true);
		
		return horse;
	}

	public static boolean wasSummoned(Entity e) {
		if(e != null) {
			//return e.isPermissionSet("fromtome");
			return e.getScoreboardTags().contains("fromtome");
		}
		
		return false;
	}
	public static void setSummoned(Entity e) {
		if(e != null) {
			//e.addAttachment(plugin, "fromtome", true);
			e.addScoreboardTag("fromtome");
		}
	}
	public static boolean isCustomHorse(Entity horse) {
		if(horse != null && horse instanceof AbstractHorse) {
			return horse.getScoreboardTags().contains("customhorse");
		}
		
		return false;
	}
	public static void setCustomHorse(Entity horse) {
		if(horse != null && horse instanceof AbstractHorse) {		
			horse.addScoreboardTag("customhorse");
		}
	}
	

	public static boolean hasSummoned(ItemStack tome) {
		if(tome == null)
			return false;
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null)
        	return false;
        if( compound.hasKey("hassummoned") )
        	return true;
		return false;
	}
	public static ItemStack setHasSummoned(ItemStack tome, UUID entity_uuid) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
           compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }
        
        if(entity_uuid == null) compound.remove("hassummoned");
        else compound.setString("hassummoned", entity_uuid.toString());
        nmsStack.setTag(compound);
        tome = CraftItemStack.asBukkitCopy(nmsStack);
        return tome;
	}
	public static Entity getHasSummoned(ItemStack tome) {
		if(tome == null)
			return null;
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null)
        	return null;
        if( compound.hasKey("hassummoned") ) {
        	UUID entity_uuid = UUID.fromString(compound.getString("hassummoned"));
        	return plugin.getServer().getEntity(entity_uuid);
        }
		return null;
	}

	
	
	public static Tome whichTome(ItemStack item) {
		if(item == null)
			return null;
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsStack.getTag();
        if(compound == null)
        	return null;
        if(compound != null && compound.hasKey("summonertome")) {
        	String info = compound.getString("summonertome");
        	if(info.startsWith("m")) {
				return Tome.MINECART;
			}
			else if(info.startsWith("b")) {
				return Tome.BOAT;
			}
			else if(info.startsWith("h")) {
				return Tome.HORSE;
			}
			else if(info.startsWith("H")) {
				return Tome.CUSTOM_HORSE;
			}
			else if(info.startsWith("a")) {
				return Tome.ALL;
			}
			else if(info.startsWith("A")) {
				return Tome.CUSTOM_ALL;
			}
        }
		return null;
	}
	public static boolean isTome(ItemStack item) {
		if(item == null)
			return false;
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null)
        	return false;
        if( compound.hasKey("summonertome") )
        	return true;
		return false;
	}
	private static ItemStack setTome(ItemStack i, char data, String metadata) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
           compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }
        
        compound.setString("summonertome", data+metadata);
        nmsStack.setTag(compound);
        i = CraftItemStack.asBukkitCopy(nmsStack);
        return i;
	}
	
	private static TreeSpecies get_boat_type(ItemStack tome) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if(compound == null)
        	return null;
        if(compound != null && compound.hasKey("summonertome")) {
        	String info = compound.getString("summonertome");
        	switch(info.charAt(1))
        	{
        	case 'a': return TreeSpecies.ACACIA;
        	case 'b': return TreeSpecies.BIRCH;
        	case 'd': return TreeSpecies.DARK_OAK;
        	case 'j': return TreeSpecies.JUNGLE;
        	case 'o': return TreeSpecies.GENERIC; //oak
        	case 's': return TreeSpecies.REDWOOD; //spruce
			}
        }
		return TreeSpecies.GENERIC;
	}
	private static ItemStack set_boat_type(ItemStack tome, TreeSpecies type) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
             nmsStack.setTag(compound);
             compound = nmsStack.getTag();
         }
        
        if(compound.hasKey("summonertome")) {
    		char[] info = compound.getString("summonertome").toCharArray();
        	switch(type)
        	{
        	case ACACIA: info[1] = 'a'; break;
        	case BIRCH: info[1] = 'b'; break;
        	case DARK_OAK: info[1] = 'd'; break;
        	case JUNGLE: info[1] = 'j'; break;
        	case GENERIC: info[1] = 'o'; break;
        	case REDWOOD: info[1] = 's'; break;
			}
	        compound.setString("summonertome", new String(info));
	        nmsStack.setTag(compound);
        }
        tome = CraftItemStack.asBukkitCopy(nmsStack);
		return tome;
	}

	private static ItemStack copy_horse_data(ItemStack tome, String data) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
        	compound = new NBTTagCompound();
        	nmsStack.setTag(compound);
        	compound = nmsStack.getTag();
        }
        
        compound.setString("summonertome", compound.getString("summonertome").substring(0, 2)+data);
        nmsStack.setTag(compound);
    	
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	private static String get_horse_data(ItemStack tome) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(tome);
	    NBTTagCompound compound = nmsStack.getTag();
	    if(compound != null && compound.hasKey("summonertome")) {
	    	String info = compound.getString("summonertome");
	    	if(info.length() > 2) {
	    		return info.substring(2);
	    	}
	    }
    	return "";
	}
	private static String get_horse_data(Horse horse) {
		return horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+MAIN_SEP
				+horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()+MAIN_SEP
				+horse.getJumpStrength()+MAIN_SEP
				+(horse.isAdult() ? 1 : 0)+MAIN_SEP+horse.getColor()+MAIN_SEP+horse.getStyle()+MAIN_SEP+convertInventory(horse.getInventory());
	}
	private static Horse set_horse_data(Horse horse, String data) {
		if(data.length() <= 0) return horse;
		System.out.println(data);
		String[] fields = data.split(MAIN_SEP_split);
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Double.parseDouble(fields[0]));
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Double.parseDouble(fields[1]));
		horse.setJumpStrength(Double.parseDouble(fields[2]));
		if (fields[3].equals("0")) horse.setBaby(); else horse.setAdult();
		horse.setColor(Color.valueOf(fields[4]));
		horse.setStyle(Style.valueOf(fields[5]));
		HorseInventory hinv = horse.getInventory();
		for(int i = 6; i < fields.length; i++) { //"1;1;1;???;???;k1:v1|k2:v2;k1:v1"
			Map<String, Object> tags_map = new HashMap<>();
			String item = fields[i];
			if("0".equals(item)) {
				hinv.setItem(i-6, null);
			}
			else {
				String[] tags = item.split(FIELD_SEP_split);
				for(String tag : tags) {
					String[] key_value = tag.split(KEYVAL_SEP_split);
					if("v".contains(key_value[0]))
						tags_map.put(key_value[0], Integer.parseInt(key_value[1]));
					else
						tags_map.put(key_value[0], key_value[1]);
				}
				hinv.setItem(i-6, ItemStack.deserialize(tags_map));
			}
		}
		return horse;
	}
	private static String convertInventory(HorseInventory inv) {
		String result = "";
		
		ItemStack[] content = inv.getContents();
		ItemStack is;
		for(int i = 0; i < content.length; i++) {
			is = content[i];
			if(is != null) {
				System.out.println(is.serialize());
				Map<String, Object> tags_map = is.serialize();
				Set<Map.Entry<String, Object>> entries = tags_map.entrySet();
				int j = entries.size();
				for (Map.Entry<String, Object> pair : tags_map.entrySet()) {
					result += pair.getKey() + KEYVAL_SEP + pair.getValue();
					System.out.println(pair.getKey()+":"+pair.getValue()+" (String:"+pair.getValue().getClass().getSimpleName()+")");
					j--;
					if(j > 0)
						result += FIELD_SEP;
				}
			}
			else
				result += "0";
			if(i+1 < content.length)
				result += ";";
		}
		
		return result;
	}
	
	//Horse slots and move tome to other inventories
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.isCancelled()) return;
		
		int slot = event.getRawSlot();
		if(slot < 0) return;
		
		boolean illegal = false;
		Inventory inv = event.getClickedInventory();
		InventoryAction action = event.getAction();
		
		if(inv instanceof AbstractHorseInventory) {
			AbstractHorseInventory hinv = (AbstractHorseInventory)inv;
			if(hinv.getHolder() != null && wasSummoned((Horse)hinv.getHolder()))
				if(slot == 0)
				{
					if(action != InventoryAction.CLONE_STACK && action != InventoryAction.UNKNOWN)
						illegal = true;
				}
				else if(!isCustomHorse((AbstractHorse)hinv.getHolder())) {
					if(slot < 2 || slot < 17 && hinv.getHolder() instanceof ChestedHorse && ((ChestedHorse)hinv.getHolder()).isCarryingChest())
						if(action != InventoryAction.CLONE_STACK && action != InventoryAction.UNKNOWN)
							illegal = true;
				}
				else {
					//change custom tome
					AbstractHorse horse = (AbstractHorse)hinv.getHolder();
					illegal = illegal_custom_horse(horse, (Player) horse.getOwner());
				}
		}
		else if(inv instanceof PlayerInventory && event.getView().getTopInventory() instanceof AbstractHorseInventory
				&& wasSummoned((Entity) event.getView().getTopInventory().getHolder())) {
			if( (event.getView().getItem(1) == null || event.getView().getItem(1).getType() == Material.AIR)
					&& Utils.isHorseArmor(event.getCurrentItem().getType())) {
					if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						if(isCustomHorse((AbstractHorse)event.getView().getTopInventory().getHolder())) {
							AbstractHorse horse = (AbstractHorse)event.getView().getTopInventory().getHolder();
							illegal = illegal_custom_horse(horse, (Player) horse.getOwner());
						}
						else
							illegal = true;
					}
				}
		}
		event.setCancelled(illegal);
	}
	
	private static int find_tome_slot(ItemStack[] inv, Entity entity) {
		for(int i = 0; i < inv.length; i++) {
			if(inv[i].getType() == Material.ENCHANTED_BOOK) {
				if(isTome(inv[i]) && getHasSummoned(inv[i]) == entity) {
					return i;
				}
			}
		}
		return -1;
	}
	private boolean illegal_custom_horse(AbstractHorse horse, Player p) {
		if(p.isOnline()) {
			int slot = find_tome_slot(p.getInventory().getContents(), horse);
			if(slot < 0) {
				horse.remove();
				return true;
			}
			else {
				addSavingTome(horse, p);
				return false;
			}
		}
		else
			return true;
	}
	private static void process_custom_horse(AbstractHorse horse, Player p) {
		if(p.isOnline()) {
			int slot = find_tome_slot(p.getInventory().getContents(), horse);
			if(slot < 0) {
				horse.remove();
			}
			else {
				ItemStack[] player_inv = p.getInventory().getContents();
				player_inv[slot] = copy_horse_data(player_inv[slot], get_horse_data((Horse)horse));
				p.getInventory().setContents(player_inv);
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerDropTome(PlayerDropItemEvent event) {
		Entity summoned = getHasSummoned(event.getItemDrop().getItemStack());
		if(summoned != null) {
			summoned.remove();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(wasSummoned(event.getEntity())) {
			event.setDroppedExp(0);
			event.getDrops().clear();
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for(Entity e : event.getChunk().getEntities())
			if(wasSummoned(e) && e.getPassengers().size() == 0)
				e.remove();
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if(wasSummoned(event.getVehicle())) {
			event.setCancelled(true);
			event.getVehicle().remove();
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if(wasSummoned(event.getVehicle()))
			event.getVehicle().remove();
	}
}
