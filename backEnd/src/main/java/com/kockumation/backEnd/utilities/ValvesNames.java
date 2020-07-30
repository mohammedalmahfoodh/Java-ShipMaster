package com.kockumation.backEnd.utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValvesNames {
  private List<ValveIdAndName> valvesNames ;

    public ValvesNames() {
        this.valvesNames = new ArrayList<>();
    }


    public List<ValveIdAndName> getValvesNames() {
        return valvesNames;
    }

    public void setValvesNames(List<ValveIdAndName> valvesNames) {
        this.valvesNames = valvesNames;
    }
}
