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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The RenderDoc interface.
 */

public interface RenderDocType
  extends AutoCloseable
{
  /**
   * Trigger a capture.
   */

  void triggerCapture();

  /**
   * @return The number of captures
   */

  long numberOfCaptures();

  /**
   * @return The current capture file path template
   */

  Optional<Path> captureFilePathTemplate();

  /**
   * Set the capture file path template.
   *
   * @param file The file template
   */

  void setCaptureFilePathTemplate(Path file);

  /**
   * Set an option.
   *
   * @param option The option
   */

  void setOption(
    RenderDocOptionType option);

  /**
   * Get the value of an option.
   *
   * @param option The option
   * @param <T>    The precise option type
   *
   * @return The value of an option
   */

  <T extends RenderDocOptionType> T option(
    Class<T> option);

  @Override
  void close()
    throws IOException;
}
