import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClientesService } from '../../services/clientes.service';
import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  private cliente: Cliente = new Cliente();
  private titulo:string = "Crear Cliente";
  private errores: String[];

  constructor(private clienteService: ClientesService,
  private router: Router,
  private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.cargarCliente()
  }

  cargarCliente(): void{
    this.activatedRoute.params.subscribe(params => {
      let id = params['id']
      if(id)
      {
        this.clienteService.getCliente(id).subscribe((cliente) => this.cliente = cliente)
        console.log(this.cliente)
      }
    })
  }

  public create(): void{
    this.clienteService.create(this.cliente)
    .subscribe(
      response => {
      this.router.navigate(['/clientes'])
      Swal.fire('Cliente creado', `Cliente ${this.cliente.nombre} creado con éxito!`,'success')
    },
    err => {
      this.errores = err.error.errors as String[];
      console.error('Codigo de error desde el backend: ' + err.status);
      console.error(err.error.errors);
    }
    );
  }

  update():void{
    this.clienteService.update(this.cliente).subscribe( cliente => {
      this.router.navigate(['/clientes'])
      Swal.fire('Cliente Actualizado',`Cliente ${this.cliente.nombre} actualizado con éxito!`,'success')
    },
    err => {
      this.errores = err.error.errors as String[];
      console.error('Codigo de error desde el backend: ' + err.status);
      console.error(err.error.errors);
    }
  );
  }

}
