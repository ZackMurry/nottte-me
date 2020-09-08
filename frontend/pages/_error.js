import React from 'react'
import Navbar from '../components/Navbar'
import { Typography } from '@material-ui/core';
import Link from 'next/link';

//todo style
function Error({ statusCode }) {


    return (
        <div>
            <Navbar />
            <div style={{marginTop: '30vh'}}>
                <Typography variant='h1' color='primary' style={{textAlign: 'center', marginTop: '10%'}}>
                    { statusCode }
                </Typography>
                <div style={{display: 'inline-flex', width: '100%'}}>
                    {/* todo center */}
                    <Typography variant='h2' color='primary' style={{textAlign: 'center', margin: '0 auto'}}>
                        Oops! There was an error. Click
                        <Link href='/'>
                            <div style={{display: 'inline-flex', margin: '0 15px', cursor: 'pointer', textDecoration: 'underline'}}>
                                here
                            </div>
                        </Link>
                        to return to home
                    </Typography>
                    
                    
                </div>
            </div>
        </div>
        
    )

}

Error.getInitialProps = ({ res, err }) => {
    let statusCode;
    if(res) {
        statusCode = res.statusCode
    } else if(err) {
        statusCode = err.statusCode
    } else {
        statusCode = 500
    }
    return { statusCode }
}

export default Error
