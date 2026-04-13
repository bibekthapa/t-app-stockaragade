import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

interface Post{
  userId: number;
  id: number;
  title: string;
  body: string;
}

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css'
})
export class PostListComponent implements OnInit{

  posts: Post[] = [];
  loading =  true;
  error =  '';

  constructor (private http : HttpClient){}

  ngOnInit(): void {

    this.fetchPosts().subscribe({
      next: (data) =>{
        this.posts = data;
        this.loading = false;
      },
      error: () =>{
        this.error = 'Failed to load posts';
        this.loading = false;
      }});
    
    
  }

  fetchPosts():Observable<Post[]>{
    return this.http.get<Post[]>('https://jsonplaceholder.typicode.com/posts');
  }

  

}
