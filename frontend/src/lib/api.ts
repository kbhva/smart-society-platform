import axios from 'axios';

export const api=axios.create({baseURL:import.meta.env.VITE_API_URL||'http://localhost:8080/api/v1'});
api.interceptors.request.use(c=>{const t=localStorage.getItem('accessToken');if(t)c.headers.Authorization=`Bearer ${t}`;return c});
api.interceptors.response.use(r=>r,err=>{if(err.response?.status===401&&location.pathname!=='/login'&&location.pathname!=='/register'){localStorage.removeItem('accessToken');localStorage.removeItem('auth');location.href='/login';}return Promise.reject(err)});
export type Auth={accessToken:string;tokenType:string;role:'ADMIN'|'RESIDENT';name:string;email:string};
