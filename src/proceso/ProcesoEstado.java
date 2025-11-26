/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author 58412
 */
public enum ProcesoEstado {
    NUEVO,
    LISTO,
    EJECUCION,
    ESPERANDO_IO,
    TERMINADO
}