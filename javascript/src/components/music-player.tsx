"use client"

import { Play, SkipBack, SkipForward, Volume2 } from "lucide-react"
import { Slider } from "@/components/ui/slider"

export default function MusicPlayer() {
    const handlePause = async () => {
          const res = await fetch("http://localhost:8080/pause", { method: "POST" });
        };
    const handleVolume = async () => {
              const res = await fetch("http://localhost:8080/volume", { method: "POST" });
            };
  return (
    <div className="w-full bg-white/10 border border-white/20 rounded-xl backdrop-blur-xl shadow-[inset_0_1px_1px_rgba(255,255,255,0.1)] p-4">
      <div className="flex items-center gap-4">
        {/* Album Art */}
        <div className="w-16 h-16 rounded-md bg-gradient-to-br from-purple-500/80 to-blue-500/80 flex-shrink-0 shadow-lg"></div>

        {/* Song Info */}
        <div className="flex-grow text-left">
          <h3 className="font-medium text-white truncate">Song Title</h3>
          <p className="text-sm text-white/70 truncate">Artist Name</p>
        </div>
      </div>

      {/* Player Controls */}
      <div className="mt-4 flex flex-col gap-2">
        <div className="flex items-center justify-center gap-6">
          <button className="text-white/70 hover:text-white transition-colors">
            <SkipBack size={20} />
          </button>
          <button
          onClick={handlePause}
          className="bg-white rounded-full w-10 h-10 flex items-center justify-center text-zinc-900 hover:scale-105 transition-transform">
            <Play size={20} fill="currentColor" />
          </button>
          <button className="text-white/70 hover:text-white transition-colors">
            <SkipForward size={20} />
          </button>
        </div>

        {/* Progress Bar */}
        <div className="mt-2 flex items-center gap-2">
          <span className="text-xs text-white/70">1:23</span>
          <Slider defaultValue={[33]} max={100} step={1} className="flex-grow" />
          <span className="text-xs text-white/70">3:45</span>
        </div>

        {/* Volume */}
        <div className="mt-2 flex items-center gap-2">
          <Volume2 size={16} className="text-white/70" />
          <Slider
          onValueChange={(value) => handle(value[0])}
          defaultValue={[70]} max={100} step={1} className="flex-grow" />
        </div>
      </div>
    </div>
  )
}
