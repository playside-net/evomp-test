package mp.evolution.test;

import mp.evolution.game.audio.Sound;
import mp.evolution.game.collision.SphericalCollision;
import mp.evolution.game.controls.Control;
import mp.evolution.game.entity.ped.Ped;
import mp.evolution.game.entity.pool.Pool;
import mp.evolution.game.entity.vehicle.Vehicle;
import mp.evolution.game.gps.Blip;
import mp.evolution.game.scaleform.Scaleform;
import mp.evolution.game.streaming.Door;
import mp.evolution.game.streaming.IPL;
import mp.evolution.game.streaming.Interior;
import mp.evolution.game.ui.UI;
import mp.evolution.math.Hash;
import mp.evolution.math.RGBA;
import mp.evolution.math.Vector3;
import mp.evolution.runtime.InvalidHandleException;
import mp.evolution.script.Script;
import mp.evolution.script.event.ScriptEvent;
import mp.evolution.script.event.ScriptEventKeyboardKey;

import java.util.*;

public class Test extends Script {
    private final Sound start = new Sound("Start_Squelch", "CB_RADIO_SFX");
    private final Sound end = new Sound("End_Squelch", "CB_RADIO_SFX");
    private final Sound radio = new Sound("Background_Loop", "CB_RADIO_SFX");
    private boolean streaming;

    private final Scaleform dashboard = new Scaleform(this, "dashboard");
    private final List<Checkpoint> collisions = new ArrayList<>();

    public Test() {
        setDoorLocked("hei_prop_hei_bankdoor_new", 232.6054, 214.1584, 106.4049, true);
        setDoorLocked("hei_prop_hei_bankdoor_new", 231.5123, 216.5177, 106.4049, true);
        setDoorLocked("v_ilev_trevtraildr", 1973.0499, 3815.5686, 33.7879, true);
        setDoorLocked("v_ilev_bk_door", 256.9125, 206.8366, 109.2830, false);
        setDoorLocked("v_ilev_bk_door", 265.6144, 217.7971, 109.2830, false);
        setDoorLocked("v_ilev_shrfdoor", 1855.5922, 3683.8213, 34.8928, false);
        setDoorLocked("v_ilev_shrf2door", -442.73795, 6015.3564, 32.2838, false);
        setDoorLocked("v_ilev_shrf2door", -444.43552, 6017.0537, 32.3005, false);
        setDoorLocked("v_ilev_bank4door02", -111.39079, 6463.931, 32.2215, false);
        setDoorLocked("prop_com_gar_door_01", 484.6064, -1315.6572, 29.5318, false);
        setDoorLocked("v_ilev_ta_door", 321.8085, 178.3599, 103.6782, false);
        setDoorLocked("prop_arm_gate_l", -1128.4692, -1587.9862, 4.683294, false);
        setDoorLocked("prop_arm_gate_l", -1130.697, -1589.5139, 4.7038, false);

        IPL mazeArena = new IPL("SP1_10_real_interior");
        mazeArena.load(this, r -> {});

        IPL luxor = new IPL("apa_v_mp_h_01_c");
        luxor.load(this, r -> {
            Vector3 c = new Vector3(-785.47, 322.6, 187.91);
            try {
                Interior i = Interior.fromPos(this, c);
                System.out.println("Found ipl id " + i.handle());
            } catch (InvalidHandleException e) {
                System.err.println("No interior at coords");
            }
        });
        new IPL("CS3_05_water_grp1").load(this, r -> {});
        new IPL("CS3_05_water_grp2").load(this, r -> {});
        new IPL("canyonriver01").load(this, r -> {});
        new IPL("railing_start").load(this, r -> {});

        Interior nightClub = Interior.fromPos(this, new Vector3(-1604.664, -3012.583, -79.9999));
        if (nightClub != null) {
            nightClub.load(this, r -> {
                System.out.println("Loaded night club");
                String[] props = {
                    "Int01_ba_security_upgrade",
                    "Int01_ba_equipment_setup",
                    "Int01_ba_Style01",
                    //"Int01_ba_Style02",
                    //"Int01_ba_Style03",
                    //"DJ_01_Lights_01",
                    //"DJ_01_Lights_02",
                    //"DJ_01_Lights_02",
                    "DJ_01_Lights_04",
                    "Int01_ba_style01_podium",
                    //"Int01_ba_style02_podium",
                    "int01_ba_lights_screen",
                    "Int01_ba_Screen",
                    "Int01_ba_bar_content",
                    "Int01_ba_booze_01",
                    "Int01_ba_booze_02",
                    "Int01_ba_booze_03",
                    "Int01_ba_dj01",
                    "Int01_ba_lightgrid_01",
                    "Int01_ba_Clutter",
                    "Int01_ba_clubname_08",
                    "Int01_ba_dry_ice",
                    "Int01_ba_deliverytruck"
                };
                for (String prop : props) {
                    nightClub.setPropEnabled(prop, true);
                }
                System.out.println("Enabled props");
                nightClub.refresh();
                System.out.println("Refreshed");
            });
        }
    }

    void drawShowRoom(String type, Ped ped, int mode, Vector3 pos) {
        invokeVoid(0x98C4FE6EC34154CAL, type, ped, mode, pos);
    }

    @Override
    public void frame() {
        Ped player = Ped.local(this);
        Vehicle vehicle = player.getVehicleUsing();
        if (vehicle != null) {
            /*graphics.drawMarker(43, vehicle.getPosition().subtract(Vector3.UNIT_Z), Vector3.UNIT_X, vehicle.getRotation(2), new Vector3(4, 2, 2), new RGBA(0.7F, 0.5F, 0F, 0.7F), false, false, false, null, null, false);
            graphics.drawMarker(22, vehicle.getPosition(), Vector3.UNIT_Z, vehicle.getRotation(2), new Vector3(2, 2, 2), new RGBA(0F, 0.5F, 0F, 0.7F), false, false, true, null, null, false);*/

            int step = 15;
            int i = 0;
            //ui.drawText("~y~ Light: ~w~" + Integer.toBinaryString(vehicle.getLightFlags()), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.MONOSPACE, 0.35F, 0.35F);
            ui.drawText("~y~ Next gear: ~w~" + vehicle.getNextGear(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Current gear: ~w~" + vehicle.getCurrentGear(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ High gear: ~w~" + vehicle.getHighGear(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ RPM: ~w~" + vehicle.getCurrentRPM(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Clutch: ~w~" + vehicle.getClutch(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Dashboard Speed: ~w~" + vehicle.getDashboardSpeed(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Throttle: ~w~" + vehicle.getThrottle(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Throttle power: ~w~" + vehicle.getThrottlePower(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Steering scale: ~w~" + vehicle.getSteeringScale(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Steering angle: ~w~" + vehicle.getSteeringAngle(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Handbrake: ~w~" + vehicle.isHandbrake(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Engine starting: ~w~" + vehicle.isEngineStarting(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Engine on: ~w~" + vehicle.isEngineOn(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Engine temp: ~w~" + vehicle.getEngineTemperature(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Brake power: ~w~" + vehicle.getBrakePower(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Turbo: ~w~" + vehicle.getTurbo(), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            ui.drawText("~y~ Interior color: ~w~" + Integer.toHexString(vehicle.getInteriorColor()), 2, 5 + (i++) * step, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
        }
        /*Vehicle near = player.getClosestVehicle(50);
        if (near != null && horn) {
            near.invokeVoid(0x9C11908013EA4715L);
        }*/
        /*Vector3 pos = player.getPosition();
        Prop door = Prop.findNearest(this, pos, 15, joaat("v_ilev_bk_vaultdoor"));
        if (door != null) {
            door.setHeading(-20);
            door.setPositionFreeze(true);
        }
        for (Pool<Prop>.Entry pr : props) {
            Vector3 p = pr.getPosition();
            if (p.distance(pos) < 15) {
                Prop prop = pr.request();
                if (Arrays.binarySearch(GAS_PUMPS, prop.getModelHash()) != -1) {
                    prop.setBreakable(false);
                    graphics.drawMarker(0, p, Vector3.ZERO, Vector3.ZERO, Vector3.ONE.multiply(1.5F), RGBA.WHITE,
                        false, false, false, null, false);
                }
            }
        }

        Prop tl = Prop.findNearest(this, pos, 15, joaat("prop_traffic_01d"));
        if (tl != null) {
            tl.setBreakable(false);
            graphics.drawMarker(0, tl.getPosition(), Vector3.ZERO, Vector3.ZERO, Vector3.ONE.multiply(1.5F), RGBA.WHITE,
                false, false, false, null, false);
        }*/

        /*float distance = 10;
        Vector3 start = Camera.Gameplay.getPosition(this);
        Vector3 dir = Camera.Gameplay.getDirection(this);
        Vector3 end = start.add(dir.multiply(distance));
        ProbeResult ray = new Ray(this, start, end, 1 + 2 + 4 + 8 + 16, player, 7).getResult(true);
        if (ray.hit) {
            graphics.drawLine(start, ray.end, RGBA.WHITE);
            ui.drawText(ray.toString(), 2, 2, RGBA.WHITE, UI.Font.CHALET_LONDON, 0.35F, 0.35F);
            Entity entity = ray.entity;
            graphics.drawMarker(0, end.add(Vector3.UNIT_Z), Vector3.UNIT_Z, Vector3.UNIT_Z, new Vector3(1, 1, 1), new RGBA(0.7F, 0.5F, 0F, 0.7F), false, false, true, null, null, false);
            if (entity != null) {
                if (controls.isDisabledJustPressed(ControlGroup.MOVE, Control.COVER)) {
                    if (entity instanceof Vehicle) {
                        Vehicle veh = (Vehicle) entity;
                        veh.setNeedsHotwire(true);
                        veh.startAlarm(5 * 1000);
                    } else {
                        entity.destroy();
                    }
                }
            }
        }*/
        for (Checkpoint collision : collisions) {
            collision.tick(player.getPosition());
        }
    }

    final Set<Control> ctrl = new HashSet<>();

    private void setDoorLocked(String model, double x, double y, double z, boolean locked) {
        Door door = new Door(Hash.joaat(model));
        door.setLocked(new Vector3(x, y, z), locked, Vector3.UNIT_Y.multiply(50));
    }

    boolean horn;

    @Override
    public boolean event(ScriptEvent event) {
        if (event instanceof ScriptEventKeyboardKey) {
            ScriptEventKeyboardKey e = (ScriptEventKeyboardKey) event;
            Ped player = Ped.local(this);
            if (e.isUp) {
                switch (e.key) {
                    case NUMPAD0: {
                        Blip blip = new Blip(this, player.getPosition());
                        Random rand = new Random();
                        int color = rand.nextInt(5);
                        blip.setSprite(1);
                        blip.setColor(color);
                        blip.setRoute(true);
                        blip.setRouteColor(color);
                        blip.setNumberIndicator(color);
                        String chars = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
                        StringBuilder title = new StringBuilder();
                        for (int i = 0; i < 30; i++) {
                            title.append(chars.charAt(rand.nextInt(33)));
                        }
                        blip.setTitle(title.toString());
                        invokeVoid(0x82CEDC33687E1F50L, true);
                        blip.invokeVoid(0x82CEDC33687E1F50L, true);
                        collisions.add(new Checkpoint(player.getPosition(), 1.5));
                        break;
                    }
                    case LEFT:
                    case RIGHT: {
                        Vehicle veh = player.getVehicleIn(false);
                        if (veh != null) {
                            boolean left = e.key == ScriptEventKeyboardKey.KeyCode.LEFT;
                            boolean old = veh.isIndicatorLight(left);
                            System.out.println((left ? "left" : "right") + " indicator: " + old + " set to " + !old);
                            veh.setIndicatorLight(left, !old);
                        }
                        break;
                    }
                }
            }
            switch (e.key) {
                case NUMPAD2:
                    if (e.isUp) {
                        radio.stop();
                        end.playFrontend();
                    } else if (!e.wasDownBefore) {
                        radio.playFrontend();
                        start.playFrontend();
                    }
                    break;
                case NUMPAD7:
                    if (e.isUp) {
                        invokeVoid(0x068E835A1D0DC0E3L, "FocusIn");
                    } else if (!e.wasDownBefore) {
                        invokeVoid(0x2206BF9A37B7F724L, "FocusIn", 0, true);
                    }
                    break;
                case F5:
                    if (!e.isUp && !e.wasDownBefore) {
                        horn = !horn;
                        Vehicle near = player.getClosestVehicle(50);
                        if (near != null) {
                            if (horn) {
                                near.invokeVoid(0x76D683C108594D0EL, true);
                                near.startHorn(1000, Hash.joaat("HELDDOWN"), true);
                            } else {
                                near.invokeVoid(0x76D683C108594D0EL, false);
                            }
                        }
                    }
                    break;
                case NUMPAD9:
                    if (e.isUp) {
                        for (Pool<Vehicle>.Entry entry : Vehicle.getPool()) {
                            entry.request().explode(true, false);
                        }
                    }
            }
        }
        return false;
    }

    class Checkpoint extends SphericalCollision {
        long creationTime = System.currentTimeMillis();
        boolean passed;
        Sound sound = new Sound("CHECKPOINT_NORMAL", "HUD_MINI_GAME_SOUNDSET");

        public Checkpoint(Vector3 center, double radius) {
            super(center, radius);
        }

        boolean isNew() {
            return System.currentTimeMillis() - creationTime <= 10 * 1000L;
        }

        @Override
        public void tick(Vector3 player) {
            super.tick(player);
            if (!passed) {
                RGBA color = isNew() ? new RGBA(0.7F, 0.5F, 0F, 0.7F) : new RGBA(0.7F, 0.7F, 0.7F, 0.7F);
                graphics.drawMarker(1, center.subtract(Vector3.UNIT_Z), Vector3.UNIT_X, Vector3.ZERO, Vector3.fromScalar(radius), color, false, false, false, null, null, false);
            }
        }

        @Override
        protected void onPlayerEnter() {
            Ped local = Ped.local(Test.this);
            Vehicle vehicle = local.getVehicleUsing();
            if (vehicle != null && !passed && !isNew()) {
                passed = true;
                sound.playAt(center);
            }
        }
    }
}
