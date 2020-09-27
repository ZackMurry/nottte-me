import Head from 'next/head'
import '../styles/Home.module.css'
import { IconButton, Typography } from '@material-ui/core'
import Navbar from '../components/Navbar'
import { useEffect, useRef, useState } from 'react'
import SampleEditor from '../components/SampleEditor'
import ArrowDownwardIcon from '@material-ui/icons/ArrowDownward';

export default function Home() {

    const [ showEditor, setShowEditor ] = useState(false)
    const [ previewStep, setPreviewStep ] = useState(1)

    const contentEl = useRef(null)
    const stepTwoEl = useRef(null)
    const stepThreeEl = useRef(null)
    const endingEl = useRef(null)

    const handleScroll = () => {
        if(window.pageYOffset < 825) {
            if(showEditor) {
                setShowEditor(false)
                setPreviewStep(1)
            }
        } else if(window.pageYOffset >= 825) {
            if(!showEditor) {
                setShowEditor(true)
            }
        }
    }

    useEffect(() => {
        document.addEventListener('scroll', handleScroll)
        return () => {
            document.removeEventListener('scroll', handleScroll)
        }
    })

    const handleGoToContent = () => {
        contentEl.current.scrollIntoView()
    }

    const moveToNextStep = () => {
        if(previewStep == 1) {
            stepTwoEl.current.scrollIntoView()
            setPreviewStep(2)
        } else if(previewStep == 2) {
            stepThreeEl.current.scrollIntoView()
            setPreviewStep(3)
        } else if(previewStep == 3) {
            endingEl.current.scrollIntoView()
            setPreviewStep(4)
        }
    }

    return (
        <div style={{margin: 0, padding: 0, overflowX: 'hidden', width: '100%'}} onScroll={handleScroll}>
            {/* head */}
            <Head>
                <title>nottte.me</title>
            </Head>
            
            <Navbar />

            {/* hero */}
            <div>
                <Typography color='primary' style={{fontSize: 128, fontFamily: 'Fjalla One', marginLeft: '7.5vw', marginTop: '10%'}}>
                    take notes.
                </Typography>
                <Typography color='primary' style={{fontSize: 48, marginLeft: '7.5vw', marginTop: '-2.5vh', fontWeight: 300}}>
                    the place for speedy writing
                </Typography>
                <div style={{width: '20%', margin: '0 auto', marginTop: '25vh', display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
                    <div>
                        <Typography color='primary' variant='h5'>
                            Try it out
                        </Typography>
                    </div>
                    <div>
                        <IconButton onClick={handleGoToContent}>
                            <ArrowDownwardIcon color='primary' fontSize='large' />
                        </IconButton>
                    </div>
                    
                </div>

                
            </div>

            

            <div style={{marginTop: '-30vh', marginLeft: '-10vw'}} className={showEditor ? 'fixed-preview-editor' : 'absolute-preview-editor'} >
                <SampleEditor 
                    className='sample-editor' 
                    step={previewStep}
                    moveToNextStep={moveToNextStep}
                />
            </div>
            <div ref={contentEl} style={{position: 'absolute', left: 0, top: '75vh'}}></div>
            <div style={{marginTop: '32.5vh', height: '359vh'}}>
                {/* step one */}
                <div style={{width: '37.5vw'}}>
                    <Typography variant='h3' color='primary' style={{textAlign: 'right'}}>
                        Try changing the text
                    </Typography>
                    <Typography 
                        variant='h5' 
                        color='primary' 
                        style={{textAlign: 'right', fontWeight: 300, marginTop: '1vh'}}
                    >
                        While our platform has a lot of capabilities, <br />
                        it's nice to know that you can regularly edit text.
                    </Typography>
                </div>

                {/* step two */}
                <div ref={stepTwoEl} style={{marginTop: '50vh', width: '37.5vw'}}>
                    <Typography variant='h3' color='primary' style={{paddingTop: '25vh', textAlign: 'right'}}>
                        Try using a text shortcut
                    </Typography>
                    <Typography 
                        variant='h5'
                        color='primary'
                        style={{textAlign: 'right', fontWeight: 300, marginTop: '1vh'}}
                    >
                        Text shortcuts insert text at your current position. <br />
                        We went ahead and made one for you to try out. <br />
                        Try pressing control and L on your keyboard
                    </Typography>
                </div>

                {/* step three */}
                <div ref={stepThreeEl} style={{marginTop: '50vh', width: '37.5vw'}}>
                    <Typography variant='h3' color='primary' style={{paddingTop: '25vh', textAlign: 'right'}}>
                        Apply a style shortcut
                    </Typography>
                    <Typography 
                        variant='h5'
                        color='primary'
                        style={{textAlign: 'right', fontWeight: 300, marginTop: '1vh'}}
                    >
                        Style shortcuts apply a style to a region. <br />
                        If you have something selected, they will apply <br />
                        styles to the selected region. If not, they activate <br />
                        the style, so that whatever you type will have that style. <br />
                        You can press control and Q on your keyboard to use <br />
                        one that we made for you.
                    </Typography>
                </div>

                <div ref={endingEl} style={{marginTop: '50vh', width: '37.5vw'}}>
                    <Typography variant='h1' color='primary' style={{paddingTop: '25vh', textAlign: 'right'}}>
                        Sign up
                    </Typography>
                </div>

            </div>
        
        </div>
    )
}
