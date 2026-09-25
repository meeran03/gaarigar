"""Targeted source release check. Reports paths, never matched values."""
from pathlib import Path
import re,sys
root=Path(__file__).resolve().parents[1]
patterns=[re.compile(rb'AKIA[0-9A-Z]{16}'),re.compile(rb'AIza[0-9A-Za-z_-]{30,}'),re.compile(rb'(?:sk|rk)_(?:live|test)_[0-9A-Za-z]{20,}'),re.compile(rb'-----BEGIN (?:RSA )?PRIVATE KEY-----'),re.compile(rb'gh[pousr]_[0-9A-Za-z]{30,}')]
problems=[]
for p in root.rglob('*'):
 if not p.is_file() or any(x in p.parts for x in ['.git','target','build','.gradle']):continue
 if p.name in ['google-services.json','local.properties'] or 'service-account' in p.name or p.suffix in ['.jks','.keystore','.zip'] or p.name.startswith('.env') and p.name!='.env.example':problems.append((str(p.relative_to(root)),'private configuration'))
 if any(pattern.search(p.read_bytes()) for pattern in patterns):problems.append((str(p.relative_to(root)),'credential pattern'))
for problem in problems:print(*problem)
if problems:sys.exit(1)
print('Targeted public-source check passed.')
