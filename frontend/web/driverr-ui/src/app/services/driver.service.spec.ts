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
  
  it('activateDriver posts to /api/drivers/activate', () => {
    const activationDto = { activationToken: 'token123', password: 'newpass' };
    service.activateDriver(activationDto).subscribe();
    const req = httpMock.expectOne('http://localhost:8081/api/drivers/activate');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(activationDto);
    req.flush({});
  });

  it('getWorkingHours retrieves driver hours', () => {
    service.getWorkingHours('driver-1').subscribe((hours) => {
      expect(hours).toBe(6.5);
    });
    const req = httpMock.expectOne('http://localhost:8081/api/drivers/driver-1/working-hours');
    expect(req.request.method).toBe('GET');
    req.flush(6.5);
  });

  it('getDriver retrieves driver info', () => {
    service.getDriver('driver-1').subscribe((driver) => {
      expect(driver.id).toBe('driver-1');
    });
    const req = httpMock.expectOne('http://localhost:8081/api/drivers/driver-1');
    expect(req.request.method).toBe('GET');
    req.flush({ id: 'driver-1', firstName: 'Test' });
  });
});

