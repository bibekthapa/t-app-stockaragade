import { Component } from '@angular/core';
import { StockDashboardComponent } from './stock-dashboard/stock-dashboard.component';
import { ChatComponent } from './chat/chat.component';
import { FloatingChatComponent } from "./floating-chat/floating-chat.component";
import { PdfUploadComponent } from './pdf-upload/pdf-upload.component';

@Component({
  selector: 'app-root',
  imports: [StockDashboardComponent, FloatingChatComponent,PdfUploadComponent],
  template: '<app-stock-dashboard></app-stock-dashboard><app-floating-chat></app-floating-chat><app-pdf-upload></app-pdf-upload>'
})
export class AppComponent {
  title = 'trade-app';
}
