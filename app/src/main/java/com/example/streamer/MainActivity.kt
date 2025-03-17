class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            val streamKey = binding.etStreamKey.text.toString()
            val fileUri = getFileUri() // Implement file selection
            
            if(streamKey.isNotEmpty() && fileUri != null) {
                startStreamingService(fileUri, streamKey)
            }
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, StreamingService::class.java))
        }
    }

    private fun startStreamingService(fileUri: Uri, streamKey: String) {
        val serviceIntent = Intent(this, StreamingService::class.java).apply {
            putExtra("FILE_URI", fileUri.toString())
            putExtra("STREAM_KEY", streamKey)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun getFileUri(): Uri? {
        // Implement file picker logic
        return null
    }
}
