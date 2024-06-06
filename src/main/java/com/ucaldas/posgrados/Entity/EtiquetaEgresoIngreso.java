package com.ucaldas.posgrados.Entity;

/*
 * Se usa para saber si un egreso o ingreso es el mismo que se presupuestó ya sea con el mismo valor o con otro valor, 
 * o si es un egreso o ingreso que no estaba presupuestado
 */
public enum EtiquetaEgresoIngreso {
    DELPRESUPUESTO_MISMOVALOR, DELPRESUPUESTO_OTROVALOR, FUERADELPRESUPUESTO

}
