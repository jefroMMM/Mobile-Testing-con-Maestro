"""Derive a reviewable summary from the original JUnit and Maestro command logs."""
from pathlib import Path
import json
import hashlib
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
evidence = root / 'evidencias/ci'
summary = {
    'source': 'Original GitHub Actions artifact taskflow-delivery, run 40982924351',
    'runUrl': 'https://github.com/jefroMMM/Mobile-Testing-con-Maestro/actions/runs/40982924351',
    'testedCommit': (evidence/'tested-commit.txt').read_text().strip(),
    'executionStartedGuatemala': (evidence/'date-guatemala.txt').read_text().strip(),
    'device': 'Android SDK built for x86_64, API 29 / Android 10, pixel_2 AVD',
    'maestro': (evidence/'maestro-version.txt').read_text().strip(),
    'executions': [],
}
for scenario, count in [('01',1),('02',1),('03',1),('suite',3),('extra',1)]:
    path = evidence/scenario/'results.xml'
    xml = ET.parse(path)
    cases = xml.findall('.//testcase')
    suites = xml.findall('.//testsuite')
    passed = len(cases)==count and all(
        case.get('status')=='SUCCESS' and case.find('failure') is None and case.find('error') is None
        for case in cases)
    verified_commands=0
    conditions=0
    for command_path in (evidence/scenario).rglob('commands.json'):
        for command in json.loads(command_path.read_text()):
            completed = command['metadata']['status']=='COMPLETED'
            verified_commands += completed
            conditions += completed and 'assertConditionCommand' in command['command']
    summary['executions'].append({
        'scenario': scenario, 'expectedCases': count, 'observedCases': len(cases),
        'result': 'aprobado' if passed else 'fallido',
        'durationSeconds': sum(float(s.get('time','0')) for s in suites),
        'completedCommands': verified_commands,
        'verifiedConditionsIncludingWaits': conditions,
        'junit': str(path.relative_to(root)).replace('\\','/'),
        'cases': [{'name':c.get('name'),'status':c.get('status'),'time':c.get('time')} for c in cases],
    })
apk=root/'output/apk/taskflow-debug.apk'
expected=(evidence/'build/apk-sha256.txt').read_text().split()[0]
summary['apkSha256']=expected
if apk.exists():
    actual=hashlib.sha256(apk.read_bytes()).hexdigest()
    if actual!=expected: raise ValueError('The local APK differs from the APK tested in CI.')
destination=root/'evidencias/resultados-verificados.json'
destination.write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(destination)
if any(e['result']!='aprobado' for e in summary['executions']): raise SystemExit(1)
