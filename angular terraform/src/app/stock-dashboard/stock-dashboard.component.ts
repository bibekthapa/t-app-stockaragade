import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';


@Component({
  selector: 'app-stock-dashboard',
  standalone: true,
  templateUrl: './stock-dashboard.component.html',
  styleUrl:'./stock-dashboard.component.css',
  imports: [CommonModule],
  providers : [ApiService]
})
export class StockDashboardComponent implements OnInit {
  
  stockData: any[] = [];
  constructor(private apiService : ApiService){}

  ngOnInit() {
   // Temporarily disabled - backend not running
   // this.apiService.getStockData().subscribe( {
   //  next: (data) => this.stockData = data.slice(0,10),
   //  error : (err) => console.error(err)
   //
   // });
  }
  
}


