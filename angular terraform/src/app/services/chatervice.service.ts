import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface ChatRequest{
  message: string;
}

export interface ChatResponse{
  reply: string ;
}


@Injectable({
  providedIn: 'root'
})

export class ChaterviceService {
 
  private apiUrl = environment.apiUrl;


  constructor(private http : HttpClient){}

  sendMessage (message : string) : Observable<ChatResponse>{
    return this.http.post<ChatResponse>(`${this.apiUrl}/api/ragchat?provider=claude` , {message});
  }
  
}
