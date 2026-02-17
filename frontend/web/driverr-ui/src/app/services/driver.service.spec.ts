import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DriverService } from './driver.service';

describe('DriverService', () => {
  let service: DriverService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DriverService]
    });

    service = TestBed.inject(DriverService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('registerDriver posts to /api/drivers/register', () => {
    const payload = { email: 'driver@test.com' };

    service.registerDriver(payload).subscribe();

    const req = httpMock.expectOne('http://localhost:8081/api/drivers/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({});
  });
});

