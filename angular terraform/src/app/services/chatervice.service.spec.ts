import { TestBed } from '@angular/core/testing';

import { ChaterviceService } from './chatervice.service';

describe('ChaterviceService', () => {
  let service: ChaterviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChaterviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
