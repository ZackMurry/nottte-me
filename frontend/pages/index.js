import Head from 'next/head'
import styles from '../styles/Home.module.css'
import { Typography } from '@material-ui/core'

export default function Home() {

  return (
    <div style={{margin: 0, padding: 0, overflowX: 'hidden', width: '100%'}}>

        {/* slope at top of white section */}
        <div style={{top: '50vh', left:0, backgroundColor: 'white', position: 'absolute', width: '100vw', height: '25vh', clipPath: 'polygon(100% 0, 100% 100%, 0% 100%)', zIndex: -1, overflowX: 'hidden'}} ></div>
        {/* white section */}
        <div style={{position: 'absolute', top: '75vh', left: 0, backgroundColor: 'white', height: '100vh', width: '100vw', zIndex: -1, overflowX: 'hidden'}}></div>
        {/* slope at bottom of white section */}
        <div style={{top: '174.95vh', left:0, backgroundColor: 'white', position: 'absolute', width: '100vw', height: '25.05vh', clipPath: 'polygon(0% 0, 100% 0%, 0% 100%)', zIndex: -1, overflowX: 'hidden'}} ></div>
        
        {/* head */}
        <Head>
            <title>Nottte.me</title>
        </Head>
        {/* navbar */}
        <div style={{backgroundColor: '#2D323E', width: '100%', display: 'flex', position: 'fixed'}}>
            <div>
                <Typography color='primary' style={{fontWeight: 100, paddingLeft: '7.5vw', paddingTop: 10, fontSize: 48, alignSelf: 'flex-end', cursor: 'pointer'}}>
                    nottte.me
                </Typography>
            </div>
            <div style={{display: 'flex', width: '80%', alignSelf: 'flex-end', justifyContent: 'flex-end'}}>
                <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                    home
                </Typography>
                <Typography color='primary' style={{fontSize: 36, fontWeight: 100, marginRight: '3vw', cursor: 'pointer'}}>
                    about
                </Typography>
                <Typography color='primary' style={{paddingRight: '7.5vw', fontSize: 36, fontWeight: 100, cursor: 'pointer'}}>
                    login
                </Typography>
            </div>
        </div>
        {/* hero */}
        <div>
            <Typography color='primary' style={{fontSize: 128, fontFamily: 'Fjalla One', marginLeft: '7.5vw', marginTop: '10%'}}>
                take notes.
            </Typography>
            <Typography color='primary' style={{fontSize: 48, marginLeft: '7.5vw', marginTop: '-2.5vh', fontWeight: 300}}>
                the place for minimal note taking
            </Typography>
        </div>
        
    </div>
  )
}
