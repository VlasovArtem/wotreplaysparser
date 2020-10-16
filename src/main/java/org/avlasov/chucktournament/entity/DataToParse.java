package org.avlasov.chucktournament.entity;

/**
 * Created By artemvlasov on 01/06/2018
 **/
public class DataToParse {

    private final boolean withUserName;
    private final boolean withVehicleName;
    private final boolean withDamageDealt;
    private final boolean withFrags;
    private boolean withExp;
    private boolean withBlockedByArmor;

    public DataToParse() {
        withUserName = true;
        withVehicleName = true;
        withDamageDealt = true;
        withFrags = true;
    }
}
