package com.ucaldas.posgrados.Entity;

/*
 * Se usa para saber si un egreso o ingreso es el mismo que se presupuestó ya sea con el mismo valor o con otro valor, 
 * o si es un egreso o ingreso que no estaba presupuestado
 * 
 * La etiqueta UTILIZADOENLAEJECUCION se usa para saber si un egreso o ingreso fue utilizado en la ejecución presupuestal y 
 * así poder saber si se puede listar como disponible para crear un nuevo CDP o no
 */
public enum EtiquetaEgresoIngreso {
    DELPRESUPUESTO_MISMOVALOR, DELPRESUPUESTO_OTROVALOR, FUERADELPRESUPUESTO, UTILIZADOENLAEJECUCION

}
