import { Component, OnInit } from '@angular/core';
import {Cliente} from './cliente';
import { ClientesService } from '../../services/clientes.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  clientes: Cliente[];
  constructor(private clienteService: ClientesService) { }

  ngOnInit()
  {
    this.clienteService.getClientes().subscribe(
      clientes => this.clientes = clientes
    );
  }

  delete(cliente: Cliente): void{
    const swalWithBootstrapButtons = Swal.mixin({
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: false,
    })

    swalWithBootstrapButtons.fire({
  title: '¿Está seguro?',
  text: `¿Seguro que desea eliminar al cliente ${cliente.nombre} ${cliente.apellido}?`,
  type: 'warning',
  showCancelButton: true,
  confirmButtonText: 'Si, eliminar!',
  cancelButtonText: 'No, cancelar!',
  reverseButtons: true
  }).then((result) => {
  if (result.value) {
    this.clienteService.delete(cliente.id).subscribe(
      response => {
        this.clientes = this.clientes.filter(cli => cli !== cliente) //Filtramos la lista para dejar fuera de la misma al cliente que hemos eliminado
                                                                    //ATENCION! De esta forma no actualizamos la lista de clientes con el backden al eliminar uno de ellos
        swalWithBootstrapButtons.fire(
          'Cliente eliminado!',
          `Cliente ${cliente.nombre} eliminado con éxito`,
          'success'
        )
      }
    )
  }})
  }

}
