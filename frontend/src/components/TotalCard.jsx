import * as React from "react";

export default function TotalCard({ dadosBanco, texto, cor }) {
  return (
    <div className="col-md-3">
        <div className="card bg-dark border-secondary shadow-sm">
            <div className="card-body text-center">
                <h6 className="text-light text-uppercase fw-bold" style={{ fontSize: "12px" }}>{texto}</h6>
                <h2 className={`mb-0 ${cor}`}>{dadosBanco || 0}</h2>
            </div>
        </div>
    </div>
  );
}