import React from 'react'
import Navbar from '../components/Navbar'
import { Typography } from '@material-ui/core';
import Link from 'next/link';

//todo style
function Error({ statusCode }) {


    return (
        <div>
            <Navbar />
            <div style={{marginTop: '15vh'}}>
                <Typography variant='h1' color='primary' style={{textAlign: 'center', marginTop: '10%'}}>
                    { statusCode }
                </Typography>
                <div>
                    {/* todo center */}
                    <Typography variant='h2' color='primary' style={{textAlign: 'center', display: 'inline-flex'}}>
                        Oops! There was an error. Click
                    </Typography>
                    {/* <Link href='/'>
                        here
                    </Link> */}
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
