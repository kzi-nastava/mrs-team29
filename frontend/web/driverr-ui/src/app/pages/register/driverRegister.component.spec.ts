/// <reference types="jasmine" />
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AdminDriverRegisterComponent } from './driverRegister.component';
import { DriverService } from '../../services/driver.service';

driverRegister.component.spec.tsdescribe('AdminDriverRegisterComponent', () => {
  let component: AdminDriverRegisterComponent;
  let driverServiceSpy: jasmine.SpyObj<DriverService>;

  beforeEach(async () => {
    driverServiceSpy = jasmine.createSpyObj('DriverService', ['registerDriver']);

    await TestBed.configureTestingModule({
      imports: [AdminDriverRegisterComponent],
      providers: [{ provide: DriverService, useValue: driverServiceSpy }]
    }).compileComponents();

    const fixture = TestBed.createComponent(AdminDriverRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('shows error when form is invalid', () => {
    component.submit();
    expect(component.errorMessage).toBe('Please fill in all required fields');
    expect(driverServiceSpy.registerDriver).not.toHaveBeenCalled();
  });

  it('submits valid form and shows success message', () => {
    driverServiceSpy.registerDriver.and.returnValue(of({}));

    component.form.setValue({
      firstName: 'Test',
      lastName: 'Driver',
      gender: 'MALE',
      username: 'test-driver',
      email: 'driver@test.com',
      password: 'pass',
      phoneNumber: '123',
      vehicleModel: 'Model X',
      vehicleType: 'STANDARD',
      registrationPlate: 'TEST-123',
      seats: 4,
      allowsPets: false,
      allowsBabies: false
    });

    component.submit();

    expect(driverServiceSpy.registerDriver).toHaveBeenCalled();
    expect(component.successMessage).toContain('Driver registered successfully');
    expect(component.errorMessage).toBe('');
  });

  it('shows error when service returns error', () => {
    driverServiceSpy.registerDriver.and.returnValue(throwError(() => ({
      error: { message: 'Registration failed' }
    })));

    component.form.setValue({
      firstName: 'Test',
      lastName: 'Driver',
      gender: 'MALE',
      username: 'test-driver',
      email: 'driver@test.com',
      password: 'pass',
      phoneNumber: '123',
      vehicleModel: 'Model X',
      vehicleType: 'STANDARD',
      registrationPlate: 'TEST-123',
      seats: 4,
      allowsPets: false,
      allowsBabies: false
    });

    component.submit();

    expect(component.errorMessage).toBe('Registration failed');
  });
});
