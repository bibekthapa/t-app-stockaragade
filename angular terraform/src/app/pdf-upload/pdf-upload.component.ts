import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpEventType, HttpClientModule } from '@angular/common/http';
import { environment } from '../environments/environment';

interface UploadedFile {
  file: File;
  previewUrl: string | null;
  progress: number;
  status: 'pending' | 'uploading' | 'success' | 'error';
  errorMessage?: string;
}

@Component({
  selector: 'app-pdf-upload',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './pdf-upload.component.html',
  styleUrls: ['./pdf-upload.component.scss']
})
export class PdfUploadComponent {

  uploadedFiles: UploadedFile[] = [];
  isDragOver = false;

   private apiUrl = environment.apiUrl;
  private readonly API_URL = `${this.apiUrl}/api/ingest`;
   readonly MAX_FILE_SIZE_MB = 10;

  constructor(private http: HttpClient) {}

  // --- Drag & Drop Handlers ---

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    const files = event.dataTransfer?.files;
    if (files) {
      this.processFiles(files);
    }
  }

  // --- File Input Handler ---

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.processFiles(input.files);
    }
  }

  // --- Core Logic ---

  private processFiles(files: FileList): void {
    Array.from(files).forEach(file => {
      if (!this.validateFile(file)) return;

      const uploadedFile: UploadedFile = {
        file,
        previewUrl: null,
        progress: 0,
        status: 'pending'
      };

      this.generatePreview(file, uploadedFile);
      this.uploadedFiles.push(uploadedFile);
      this.uploadFile(uploadedFile);
    });
  }

  private validateFile(file: File): boolean {
    if (file.type !== 'application/pdf') {
      alert(`"${file.name}" is not a PDF file.`);
      return false;
    }
    const sizeMB = file.size / (1024 * 1024);
    if (sizeMB > this.MAX_FILE_SIZE_MB) {
      alert(`"${file.name}" exceeds the ${this.MAX_FILE_SIZE_MB}MB limit.`);
      return false;
    }
    return true;
  }

  private generatePreview(file: File, uploadedFile: UploadedFile): void {
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedFile.previewUrl = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  private uploadFile(uploadedFile: UploadedFile): void {
    const formData = new FormData();
    formData.append('file', uploadedFile.file);

    uploadedFile.status = 'uploading';

    this.http.post(this.API_URL, formData, {
      reportProgress: true,
      observe: 'events'
    }).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress) {
          const total = event.total ?? 1;
          uploadedFile.progress = Math.round((event.loaded / total) * 100);
        } else if (event.type === HttpEventType.Response) {
          uploadedFile.status = 'success';
          uploadedFile.progress = 100;
        }
      },
      error: (err) => {
        uploadedFile.status = 'error';
        uploadedFile.errorMessage = err.message || 'Upload failed';
      }
    });
  }

  removeFile(index: number): void {
    this.uploadedFiles.splice(index, 1);
  }

  formatSize(bytes: number): string {
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }
}