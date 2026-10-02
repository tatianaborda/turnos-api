#!/usr/bin/env bash
# Los mismos requests con curl (-i muestra status y headers)
BASE=http://localhost:8080

curl -i "$BASE/hola?nombre=Bootcamp"

curl -i -X POST "$BASE/api/turnos" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Ana Pérez","servicio":"Corte de pelo","fechaHora":"2026-10-05T10:30:00"}'

curl -i "$BASE/api/turnos"
curl -i "$BASE/api/turnos?estado=PENDIENTE"
curl -i "$BASE/api/turnos/1"
curl -i "$BASE/api/turnos/999"

curl -i -X PUT "$BASE/api/turnos/1" \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Ana Pérez","servicio":"Corte y color","fechaHora":"2026-10-05T11:00:00"}'

curl -i -X DELETE "$BASE/api/turnos/1"
