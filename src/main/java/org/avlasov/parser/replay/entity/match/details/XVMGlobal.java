package org.avlasov.parser.replay.entity.match.details;

import lombok.Data;

import java.util.List;

@Data
public class XVMGlobal {

    private MinimapCircles minimapCircles;

    public class MinimapCircles {

        private int baseGunReloadTime;
        private boolean viewRammer;
        private int baseLoadersSkill;
        private int baseRadioDistance;
        private int viewCommanderEagleEye;
        private int viewRadiomanFinder;
        private int baseRadiomanSkill;
        private boolean viewStereoscope;
        private boolean viewVentilation;
        private boolean viewConsumable;
        private int viewRadiomanInventor;
        private List<ViewCamouflage> viewCamouflage;
        private boolean viewBrothersInArms;
        private boolean commanderSixthSense;
        private boolean viewCoatedOptics;
        private boolean isFullCrew;
        private int viewDistanceVehicle;
        private int artilleryRange;
        private int baseCommanderSkill;
        private int vehCD;
        private int shellRange;

        private class ViewCamouflage {
            private int skill;
            private String name;
        }

    }

}
