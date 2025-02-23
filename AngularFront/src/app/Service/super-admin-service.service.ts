import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { port } from 'src/env';
@Injectable({
  providedIn: 'root'
})
export class SuperAdminServiceService {

  constructor(private http:HttpClient) {
  }
  getAllEmployees(){
    return this.http.get(`${port}/INFOUSER-SERVICE/GererEmployer/getAllEmployers`);
  }
  searchEmployeesByName(search: string) {
    return this.http.get(`${port}/INFOUSER-SERVICE/GererEmployer/getAllEmployers?search=${search}`);
  }
  AddEmployer(Employer:any){
    return this.http.post(`${port}/INFOUSER-SERVICE/GererEmployer/AddEmployer`,Employer);
  }
  deleteEmployer(employerId:number){
    return this.http.put(`${port}/INFOUSER-SERVICE/GererEmployer/RefuseEmployer?id=${employerId}`,{});
  }
}
