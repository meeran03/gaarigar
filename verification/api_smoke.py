"""Exercise fixture accounts. Never prints tokens or private credentials."""
import json,os,urllib.request,urllib.error
base=os.environ.get('GAARIGAR_TEST_URL','http://127.0.0.1:3230')
password=os.environ.get('GAARIGAR_SAMPLE_PASSWORD','GaariGar-sample-2026')
def call(path,token=None,data=None,method=None,expected=200):
 headers={'Content-Type':'application/json'}
 if token:headers['Authorization']='Bearer '+token
 req=urllib.request.Request(base+path,data=json.dumps(data).encode() if data is not None else None,headers=headers,method=method)
 try:
  response=urllib.request.urlopen(req);status=response.status;raw=response.read()
 except urllib.error.HTTPError as e:status=e.code;raw=e.read()
 assert status==expected,(path,status,raw[:180])
 return json.loads(raw) if raw else None
def login(phone):return call('/api/auth/login',data={'phone':phone,'password':password})
assert call('/actuator/health')['status']=='UP'
a=login('+199955501001');b=login('+199955501002');m=login('+199955502001');v=login('+199955501099')
call('/api/customer/'+str(b['id']),a['accessToken'],expected=403)
call('/api/order/'+str(b['id']),a['accessToken'],expected=403)
call('/api/auth/register/admin',data={},expected=403)
call('/api/customer/'+str(a['id']),a['accessToken'])
services=call('/api/mechanic-standard-service/search',a['accessToken']);assert len(services)>=6
orders=call('/api/order/'+str(a['id']),a['accessToken']);assert len(orders)>=1
call('/api/category/create',a['accessToken'],data={'name':'Unauthorized'},expected=403)
print('PASS: health, login, catalog, orders, and cross-account/admin access boundaries')
# Opt in because this creates one synthetic service request.
if os.environ.get('GAARIGAR_TEST_BOOKING')=='1':
 o=call('/api/standard-order/create',a['accessToken'],data={'mechanicStandardServiceId':1,'location':{'latitude':33.6844,'longitude':73.0479},'notes':'Automated sample acceptance check','paymentMethod':'CASH'})
 oid=o['orderId']
 call('/api/order/startOrder/'+str(oid),a['accessToken'],data={},expected=403)
 call('/api/standard-order/accept/'+str(oid),m['accessToken'],data={})
 call('/api/order/startOrder/'+str(oid),m['accessToken'],data={})
 call('/api/order/complete/'+str(oid),m['accessToken'],data=1500)
 records=call('/api/order/'+str(a['id']),a['accessToken']);record=next(x for x in records if x['orderId']==oid);assert record.get('status')=='COMPLETED',record
 print('PASS: sample booking create, provider accept, start, complete; customer cannot impersonate provider')
