// Node 22+: test the real STOMP endpoint using fictional accounts only.
const base = process.env.GAARIGAR_TEST_URL || 'http://127.0.0.1:3230';
async function login(phone) {
  const r = await fetch(`${base}/api/auth/login`, {method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({phone,password:'GaariGar-sample-2026'})});
  if (!r.ok) throw new Error(`Login failed: ${r.status}`);
  return r.json();
}
function connection(token) {
  const ws = new WebSocket(base.replace(/^http/, 'ws') + '/chat');
  const frames = []; let receiver;
  ws.addEventListener('message', e => { if(receiver) {const r=receiver;receiver=null;r(String(e.data));} else frames.push(String(e.data)); });
  const next = () => new Promise((resolve,reject) => {if(frames.length)return resolve(frames.shift()); const t=setTimeout(()=>reject(new Error('STOMP timed out')),10000);receiver=x=>{clearTimeout(t);resolve(x);};});
  const ready = new Promise((resolve,reject) => {ws.addEventListener('open',resolve,{once:true});ws.addEventListener('error',()=>reject(new Error('WebSocket connection failed')),{once:true});});
  return {ws,next,async connect(){await ready;ws.send(`CONNECT\naccept-version:1.2\nhost:gaarigar\nX-Authorization:${token}\n\n\0`);if(!(await next()).startsWith('CONNECTED'))throw new Error('STOMP rejected login');}};
}
const a=await login('+199955501001'), b=await login('+199955501002');
const response=await fetch(`${base}/api/order/${a.id}`,{headers:{Authorization:`Bearer ${a.accessToken}`}});
const orders=await response.json(); const oid=orders[0].orderId;
const own=connection(a.accessToken),other=connection(b.accessToken);
try {
  await own.connect();
  own.ws.send(`SUBSCRIBE\nid:own\ndestination:/channel/${oid}\n\n\0`);
  const payload=JSON.stringify({type:'CHAT',content:'Fictional acceptance test',sender:'forged sender',channelId:'forged channel'});
  own.ws.send(`SEND\ndestination:/app/chat/${oid}/send\ncontent-type:application/json\n\n${payload}\0`);
  const frame=await own.next();if(!frame.startsWith('MESSAGE'))throw new Error('No chat message returned');
  const message=JSON.parse(frame.slice(frame.indexOf('\n\n')+2).replace(/\0.*$/s,''));
  if(message.sender!==a.phone||message.channelId!==String(oid))throw new Error('Sender/channel was not enforced');
  await other.connect();other.ws.send(`SUBSCRIBE\nid:other\ndestination:/channel/${oid}\n\n\0`);
  if(!(await other.next()).startsWith('ERROR'))throw new Error('Cross-account subscription was not rejected');
  console.log('PASS: authenticated order chat, server-enforced sender/channel, unrelated subscriber denied');
} finally {own.ws.close();other.ws.close();}
