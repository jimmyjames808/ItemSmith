package mastrjimbo.itemsmith.registry;

/**
 * Canonical activator ids, referenced by both the listeners (which fire them)
 * and {@link BuiltinComponents} (which registers a {@code SimpleActivator} for
 * each). Every id here is backed by a real event handler in {@code listener/} —
 * there are no metadata-only placeholders. Keeping the ids here keeps the
 * fire-site and the registration in sync.
 */
public final class Activators {

    // --- Interact: clicks (PlayerInteractEvent, main hand) ---
    public static final String RIGHT_CLICK = "right_click";
    public static final String RIGHT_CLICK_AIR = "right_click_air";
    public static final String RIGHT_CLICK_BLOCK = "right_click_block";
    public static final String LEFT_CLICK = "left_click";
    public static final String LEFT_CLICK_AIR = "left_click_air";
    public static final String LEFT_CLICK_BLOCK = "left_click_block";
    public static final String ANY_CLICK = "any_click";
    public static final String SNEAK_RIGHT_CLICK = "sneak_right_click";
    public static final String SNEAK_LEFT_CLICK = "sneak_left_click";
    public static final String SNEAK_RIGHT_CLICK_BLOCK = "sneak_right_click_block";
    public static final String SNEAK_LEFT_CLICK_BLOCK = "sneak_left_click_block";
    public static final String ARM_SWING = "arm_swing";

    // --- Interact: entities (PlayerInteractEntityEvent) ---
    public static final String CLICK_ENTITY = "click_entity";
    public static final String CLICK_PLAYER = "click_player";
    public static final String SNEAK_CLICK_ENTITY = "sneak_click_entity";

    // --- Inventory (InventoryClickEvent, InventoryDragEvent, open/close) ---
    public static final String INVENTORY_CLICK = "inventory_click";
    public static final String INVENTORY_DRAG = "inventory_drag";
    public static final String OPEN_INVENTORY = "open_inventory";
    public static final String CLOSE_INVENTORY = "close_inventory";

    // --- Combat: attacking ---
    public static final String PLAYER_HIT_ENTITY = "player_hit_entity";
    public static final String PLAYER_HIT_PLAYER = "player_hit_player";
    public static final String PLAYER_KILL_ENTITY = "player_kill_entity";
    public static final String PLAYER_KILL_PLAYER = "player_kill_player";

    // --- Combat: defense ---
    public static final String PLAYER_TAKE_DAMAGE = "player_take_damage";
    public static final String PLAYER_TAKE_DAMAGE_BY_ENTITY = "player_take_damage_by_entity";
    public static final String PLAYER_TAKE_DAMAGE_BY_PLAYER = "player_take_damage_by_player";
    public static final String PLAYER_TAKE_DAMAGE_BY_PROJECTILE = "player_take_damage_by_projectile";
    public static final String PLAYER_BLOCK_HIT = "player_block_hit";
    public static final String PLAYER_TARGETED = "player_targeted";

    // --- Damage causes (EntityDamageEvent, victim = player) ---
    public static final String TAKE_FALL_DAMAGE = "take_fall_damage";
    public static final String TAKE_FIRE_DAMAGE = "take_fire_damage";
    public static final String TAKE_FIRE_TICK_DAMAGE = "take_fire_tick_damage";
    public static final String TAKE_LAVA_DAMAGE = "take_lava_damage";
    public static final String TAKE_DROWN_DAMAGE = "take_drown_damage";
    public static final String TAKE_EXPLOSION_DAMAGE = "take_explosion_damage";
    public static final String TAKE_VOID_DAMAGE = "take_void_damage";
    public static final String TAKE_LIGHTNING_DAMAGE = "take_lightning_damage";
    public static final String TAKE_MAGIC_DAMAGE = "take_magic_damage";
    public static final String TAKE_WITHER_DAMAGE = "take_wither_damage";
    public static final String TAKE_THORNS_DAMAGE = "take_thorns_damage";
    public static final String TAKE_FREEZE_DAMAGE = "take_freeze_damage";
    public static final String TAKE_SUFFOCATION_DAMAGE = "take_suffocation_damage";
    public static final String TAKE_CONTACT_DAMAGE = "take_contact_damage";
    public static final String TAKE_STARVATION_DAMAGE = "take_starvation_damage";
    public static final String TAKE_SONIC_BOOM_DAMAGE = "take_sonic_boom_damage";
    public static final String TAKE_DRAGON_BREATH_DAMAGE = "take_dragon_breath_damage";

    // --- Effects ---
    public static final String PLAYER_RECEIVE_EFFECT = "player_receive_effect";
    public static final String PLAYER_EFFECT_EXPIRE = "player_effect_expire";

    // --- Death / health / xp / food ---
    public static final String PLAYER_DEATH = "player_death";
    public static final String PLAYER_RESPAWN = "player_respawn";
    public static final String REGAIN_HEALTH = "regain_health";
    public static final String EXPERIENCE_CHANGE = "experience_change";
    public static final String LEVEL_UP = "level_up";
    public static final String LEVEL_DOWN = "level_down";
    public static final String FOOD_CHANGE = "food_change";
    public static final String TOTEM_USE = "totem_use";

    // --- Durability ---
    public static final String ITEM_DURABILITY_DAMAGE = "item_durability_damage";
    public static final String ITEM_BREAK = "item_break";
    public static final String ITEM_MEND = "item_mend";

    // --- Projectiles ---
    public static final String PROJECTILE_LAUNCH = "projectile_launch";
    public static final String PROJECTILE_LAUNCH_BOW = "projectile_launch_bow";
    public static final String PROJECTILE_LAUNCH_CROSSBOW = "projectile_launch_crossbow";
    public static final String PROJECTILE_LAUNCH_TRIDENT = "projectile_launch_trident";
    public static final String READY_ARROW = "ready_arrow";
    public static final String CROSSBOW_LOAD = "crossbow_load";
    public static final String PROJECTILE_THROW = "projectile_throw";
    public static final String PROJECTILE_HIT = "projectile_hit";
    public static final String PROJECTILE_HIT_BLOCK = "projectile_hit_block";
    public static final String PROJECTILE_HIT_ENTITY = "projectile_hit_entity";
    public static final String PROJECTILE_HIT_PLAYER = "projectile_hit_player";
    public static final String PROJECTILE_ENTER_LIQUID = "projectile_enter_liquid";
    public static final String RIPTIDE = "riptide";
    public static final String EGG_THROW = "egg_throw";

    // --- Blocks / world ---
    public static final String BLOCK_BREAK = "block_break";
    public static final String BLOCK_PLACE = "block_place";
    public static final String BLOCK_DAMAGE_START = "block_damage_start";
    public static final String BLOCK_DAMAGE_STOP = "block_damage_stop";
    public static final String HARVEST_BLOCK = "harvest_block";
    public static final String FERTILIZE_BLOCK = "fertilize_block";
    public static final String BUCKET_FILL = "bucket_fill";
    public static final String BUCKET_EMPTY = "bucket_empty";
    public static final String BUCKET_ENTITY = "bucket_entity";
    public static final String SHEAR_ENTITY = "shear_entity";
    public static final String ARMOR_STAND_MANIPULATE = "armor_stand_manipulate";
    public static final String ITEM_FRAME_CHANGE = "item_frame_change";
    public static final String RING_BELL = "ring_bell";
    public static final String SIGN_EDIT = "sign_edit";

    // --- Movement / state ---
    public static final String JUMP = "jump";
    public static final String MOVE = "move";
    public static final String SNEAK = "sneak";
    public static final String UNSNEAK = "unsneak";
    public static final String SPRINT = "sprint";
    public static final String UNSPRINT = "unsprint";
    public static final String FLY_START = "fly_start";
    public static final String FLY_STOP = "fly_stop";
    public static final String GLIDE_START = "glide_start";
    public static final String GLIDE_STOP = "glide_stop";
    public static final String SWIM_START = "swim_start";
    public static final String SWIM_STOP = "swim_stop";
    public static final String TELEPORT = "teleport";
    public static final String MOUNT = "mount";
    public static final String DISMOUNT = "dismount";
    public static final String BED_ENTER = "bed_enter";
    public static final String BED_LEAVE = "bed_leave";
    public static final String ELYTRA_BOOST = "elytra_boost";
    public static final String INPUT = "input";
    public static final String POSE_CHANGE = "pose_change";

    // --- Session / world ---
    public static final String JOIN = "join";
    public static final String QUIT = "quit";
    public static final String KICK = "kick";
    public static final String CHANGE_WORLD = "change_world";
    public static final String PORTAL = "portal";
    public static final String GAMEMODE_CHANGE = "gamemode_change";
    public static final String COMMAND = "command";
    public static final String CHAT = "chat";

    // --- Item lifecycle (hand / slot) ---
    public static final String ITEM_HOLD = "item_hold";
    public static final String ITEM_UNHOLD = "item_unhold";
    public static final String EQUIP = "equip";
    public static final String UNEQUIP = "unequip";
    public static final String SWAP_HAND = "swap_hand";
    public static final String ITEM_DROP = "item_drop";
    public static final String ITEM_PICKUP = "item_pickup";
    public static final String ITEM_CONSUME = "item_consume";
    public static final String STOP_USING_ITEM = "stop_using_item";
    public static final String BOOK_EDIT = "book_edit";
    public static final String BOOK_SIGN = "book_sign";
    public static final String PICKUP_PROJECTILE = "pickup_projectile";
    public static final String PICKUP_ARROW = "pickup_arrow";
    public static final String PICKUP_TRIDENT = "pickup_trident";
    public static final String PICKUP_SPECTRAL_ARROW = "pickup_spectral_arrow";

    // --- Entity interactions ---
    public static final String FEED_ENTITY = "feed_entity";
    public static final String BREED_ENTITY = "breed_entity";
    public static final String TAME_ENTITY = "tame_entity";
    public static final String NAME_ENTITY = "name_entity";
    public static final String LEASH_ENTITY = "leash_entity";
    public static final String TRADE = "trade";

    // --- Crafting / stations ---
    public static final String CRAFT_ITEM = "craft_item";
    public static final String ENCHANT_ITEM = "enchant_item";
    public static final String SMITH_ITEM = "smith_item";
    public static final String RECIPE_DISCOVER = "recipe_discover";
    public static final String ADVANCEMENT = "advancement";

    // --- Fishing ---
    public static final String FISH_CAST = "fish_cast";
    public static final String FISH_BITE = "fish_bite";
    public static final String FISH_CATCH = "fish_catch";
    public static final String FISH_CATCH_ENTITY = "fish_catch_entity";
    public static final String FISH_IN_GROUND = "fish_in_ground";
    public static final String FISH_REEL = "fish_reel";

    // --- Passive / timer ---
    public static final String HOLD_TICK = "hold_tick";
    public static final String INVENTORY_TICK = "inventory_tick";
    public static final String EQUIP_TICK = "equip_tick";

    private Activators() {
    }
}
