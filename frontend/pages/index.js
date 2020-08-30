import Head from 'next/head'
import '../styles/Home.module.css'
import { Typography } from '@material-ui/core'
import Navbar from '../components/Navbar'

export default function Home() {

  return (
    <div style={{margin: 0, padding: 0, overflowX: 'hidden', width: '100%'}}>

        {/* head */}
        <Head>
            <title>Nottte.me</title>
        </Head>

        {/* slope at top of white section */}
        <div style={{top: '50vh', left:0, backgroundColor: 'white', position: 'absolute', width: '100%', height: '25vh', clipPath: 'polygon(100% 0, 100% 100%, 0% 100%)', zIndex: -1, overflowX: 'hidden'}} ></div>
        {/* white section */}
        <div style={{position: 'absolute', top: '75vh', left: 0, backgroundColor: 'white', height: '100vh', width: '100%', zIndex: -1, overflowX: 'hidden'}}></div>
        {/* slope at bottom of white section */}
        <div style={{top: '174.95vh', left:0, backgroundColor: 'white', position: 'absolute', width: '100%', height: '25.05vh', clipPath: 'polygon(0% 0, 100% 0%, 0% 100%)', zIndex: -1, overflowX: 'hidden'}} ></div>
        
        <Navbar />

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
