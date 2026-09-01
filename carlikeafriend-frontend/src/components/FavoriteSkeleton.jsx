import React from 'react'

export const FavoriteSkeleton = () => {

    return (
        <div className="card h-100 border-0 shadow-sm" aria-hidden="true">
            <div className="ratio ratio-16x9 placeholder-glow">
                <div className="placeholder col-12 bg-light"></div>
            </div>
            <div className="card-body">
                <p className="placeholder-glow"><span className="placeholder col-4"></span></p>
                <h5 className="placeholder-glow"><span className="placeholder col-8"></span></h5>
                <div className="py-3 my-2 border-top border-bottom placeholder-glow">
                    <span className="placeholder col-12"></span>
                </div>
                <div className="d-flex justify-content-between mt-3">
                    <span className="placeholder col-4 h4"></span>
                    <span className="placeholder col-4 btn btn-primary disabled"></span>
                </div>
            </div>
        </div>
    );
}
