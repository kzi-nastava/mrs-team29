import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('authToken');
  const isBackendRequest = req.url.includes('/api/');
  const isAuthEndpoint = req.url.includes('/api/auth/');

  if (!token || !isBackendRequest || isAuthEndpoint) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
