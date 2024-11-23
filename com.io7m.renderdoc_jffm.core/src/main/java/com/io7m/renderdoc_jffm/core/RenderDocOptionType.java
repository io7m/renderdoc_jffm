/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.renderdoc_jffm.core;

/**
 * The available RenderDoc options.
 */

public sealed interface RenderDocOptionType
{
  /**
   * Allow the application to enable vsync.
   *
   * @param allow Whether to allow
   */

  record AllowVSync(
    boolean allow)
    implements RenderDocOptionType
  {

  }

  /**
   * Allow the application to enable fullscreen.
   *
   * @param allow Whether to allow
   */

  record AllowFullscreen(
    boolean allow)
    implements RenderDocOptionType
  {

  }

  /**
   * Record API debugging events and messages.
   *
   * @param enabled Whether to enable
   */

  record APIValidation(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * Capture CPU callstacks for API events.
   *
   * @param enabled Whether to enable
   */

  record CaptureCallstacks(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * When capturing CPU callstacks, only capture them from actions.
   *
   * @param enabled Whether to enable
   */

  record CaptureCallstacksOnlyActions(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * Specify a delay in seconds to wait for a debugger to attach.
   *
   * @param seconds The number of seconds to delay
   */

  record DelayForDebugger(
    int seconds)
    implements RenderDocOptionType
  {

  }

  /**
   * Verify buffer access.
   *
   * @param enabled Whether to enable
   */

  record VerifyBufferAccess(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * Hooks any system API calls that create child processes, and injects
   * RenderDoc into them recursively with the same options.
   *
   * @param enabled Whether to enable
   */

  record HookIntoChildren(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * By default, RenderDoc only includes resources in the final capture necessary
   * for that frame, this allows you to override that behaviour.
   *
   * @param enabled Whether to enable
   */

  record RefAllResources(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * In APIs that allow for the recording of command lists to be replayed later,
   * RenderDoc may choose to not capture command lists before a frame capture is
   * triggered, to reduce overheads. This means any command lists recorded once
   * and replayed many times will not be available and may cause a failure to
   * capture.
   *
   * @param enabled
   */

  record CaptureAllCmdLists(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * Mute API debugging output when the API validation mode option is enabled.
   *
   * @param enabled
   */

  record DebugOutputMute(
    boolean enabled)
    implements RenderDocOptionType
  {

  }

  /**
   * Define a soft memory limit which some APIs may aim to keep overhead under
   * where possible. Anything above this limit will where possible be saved
   * directly to disk during capture.
   *
   * @param megabytes The size limit in megabytes
   */

  record SoftMemoryLimit(
    int megabytes)
    implements RenderDocOptionType
  {

  }
}
